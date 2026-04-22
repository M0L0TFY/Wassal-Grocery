package com.Wassal.service;

import com.Wassal.dto.CartItemResponse;
import com.Wassal.dto.CartRequest;
import com.Wassal.dto.CartResponse;
import com.Wassal.dto.ProductResponse;
import com.Wassal.exception.InsufficientException;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.CartMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
cart:userId:storeId -> {
    "productId": "quantity"
}
*/

@Service
@RequiredArgsConstructor
@Validated
@PreAuthorize("#userId == authentication.principal.id")
public class CartService {
    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;
    private final CartMapper cartMapper;

    private static final int cartTTL = 14;

    public CartResponse getCart(Long userId, Long storeId) {
        //Generate Redis key
        String key = generateKey(userId, storeId);
        //Get cart data map, "productId": "quantity"
        Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(key);
        //Return an empty CartResponse with current storeId, empty items list, and zero totalPrice
        if (cartEntries.isEmpty()) return new CartResponse(storeId, List.of(), BigDecimal.ZERO);
        //Convert the "productId" Objects returned from Redis to a list of Strings
        List<String> productIds = cartEntries.keySet().stream().map(Object::toString).toList();
        //Get the list of products from their ids
        List<ProductResponse> products = productService.getProductsByIds(productIds);
        //Convert the list of ProductResponses to CartItemResponses
        List<CartItemResponse> itemResponses = products.stream()
                .map(p -> {
                    //Get the quantity Object value from the "productId" Object key
                    int quantity = Integer.parseInt(cartEntries.get(p.id()).toString());
                    return cartMapper.toCartItemResponse(p, quantity);
                })
                .toList();
        //Calculate the totalPrice from the subTotals of the list of CartItemResponses
        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(storeId, itemResponses, totalPrice);
    }

    public void addItemToCart(Long userId, Long storeId, @Valid CartRequest request) {
        //Generate Redis key
        String key = generateKey(userId, storeId);
        //Calculate the new quantity (since if we add an existing item it will add to the quantity)
        int oldQuantity = getCartItemQuantity(key, request.productId());
        int newQuantity = oldQuantity + request.quantity();
        //Check if product belongs to the store and the new quantity is within the store's stock
        validateStock(request.productId(), storeId, newQuantity);
        //Add a new product entry to Redis
        redisTemplate.opsForHash().increment(key, request.productId(), request.quantity());
        //Expire the Redis key after cartTTL days
        redisTemplate.expire(key, cartTTL,TimeUnit.DAYS);
    }

    public void updateItemQuantity(Long userId, Long storeId, @Valid CartRequest request) {
        //Generate Redis key
        String key = generateKey(userId, storeId);
        //Check if product exists in the cart
        if (Boolean.FALSE.equals(redisTemplate.opsForHash().hasKey(key, request.productId())))
            throw new ResourceNotFoundException("Product not found in cart, add it first.");
        //Remove product from cart if quantity is less than 1
        if (request.quantity() <= 0) {
            removeItemFromCart(userId, storeId, request.productId());
            return;
        }
        //Check if product belongs to the store and the new quantity is within the store's stock
        validateStock(request.productId(), storeId, request.quantity());
        //Update product quantity
        redisTemplate.opsForHash().put(key, request.productId(), String.valueOf(request.quantity()));
        //Expire the Redis key after cartTTL days
        redisTemplate.expire(key, cartTTL,TimeUnit.DAYS);
    }

    public void removeItemFromCart(Long userId, Long storeId, String productId) {
        //Delete product in cart from Redis
        redisTemplate.opsForHash().delete(generateKey(userId, storeId), productId);
    }

    public void clearCart(Long userId, Long storeId) {
        String key = generateKey(userId, storeId);
        redisTemplate.delete(key);
    }

    private String generateKey(Long userId, Long storeId) {
        return "cart:" + userId + ":" + storeId;
    }

    //Validate if the quantity requested exceeds the store's product stock quantity
    private void validateStock(String productId, Long storeId, int quantity) {
        //Get the product if it belongs to the store
        ProductResponse product = productService.getValidatedProduct(productId, storeId);
        if (quantity > product.quantity()) throw new InsufficientException("Requested quantity exceeds the limit.");
    }
    //Get the current item quantity from cart
    private int getCartItemQuantity(String key, String productId) {
        Object stock = redisTemplate.opsForHash().get(key, productId);
        return (stock == null) ? 0 : Integer.parseInt(stock.toString());
    }

}
