package com.Wassal.service;

import com.Wassal.dto.CartResponse;
import com.Wassal.dto.CheckoutRequest;
import com.Wassal.dto.OrderResponse;
import com.Wassal.exception.InsufficientException;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.OrderMapper;
import com.Wassal.model.*;
import com.Wassal.repository.OrderRepository;
import com.Wassal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final StoreService storeService;
    private final OrderMapper orderMapper;
    private final UserAddressService userAddressService;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwnerOfOrder(#orderId)")
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with this id not found."));
        return orderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAllByUserId(userId, pageable);
        return ordersPage.map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @storeSecurity.isManagerOfStore(#storeId)")
    public Page<OrderResponse> getOrdersByStoreId(Long storeId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAllByStoreId(storeId, pageable);
        return ordersPage.map(orderMapper::toDTO);
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    public OrderResponse checkout(CheckoutRequest request, Long storeId, Long userId){
        //Save a snapshot of UserAddress, store and user references
        UserAddress userAddress = userAddressService.findUserAddressById(request.userAddressId());
        Store store = storeService.findStoreById(storeId);
        User user = userRepository.getReferenceById(userId);
        //Get current cart
        CartResponse cart = cartService.getCart(userId, storeId);
        if (cart.items().isEmpty()) throw new InsufficientException("Can't create an order from an empty cart.");
        //Prepare Order object and store it in database
        Order order = orderMapper.toEntity(cart, userAddress, store, user);
        //Set Order in OrderItem
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order);
            }
        }
        Order savedOrder = orderRepository.save(order);
        //Decrease product stock quantity
        cart.items().forEach(item ->
                productService.decreaseStock(item.productId(), item.quantity())
        );
        //Clear current cart
        cartService.clearCart(userId, storeId);
        //Return OrderResponse
        return orderMapper.toDTO(savedOrder);
    }

}
