package com.Wassal.service;

import com.Wassal.dto.ProductRequest;
import com.Wassal.dto.ProductResponse;
import com.Wassal.exception.AlreadyExistsException;
import com.Wassal.exception.InsufficientException;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.ProductMapper;
import com.Wassal.model.Product;
import com.Wassal.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductResponse> getProductsByStoreId(Long storeId, Pageable pageable) {
        return productRepository.findAllByStoreId(storeId, pageable).map(productMapper::toDTO);
    }
    public ProductResponse getProductById(String productId) {
        return productRepository.findById(productId).map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    }

    @PreAuthorize("@storeSecurity.isManagerOfStore(#request.storeId())")
    public ProductResponse createProduct(@Valid ProductRequest request) {
        //Check for duplicate product by name, brand and store id
        if (productRepository.existsByStoreIdAndNameAndBrand(request.storeId(), request.name(), request.brand()))
            throw new AlreadyExistsException("Product with this name and brand already exists in this store.");
        Product product = productMapper.toEntity(request);
        Product newProduct = productRepository.save(product);
        return productMapper.toDTO(newProduct);
    }

    @PreAuthorize("@storeSecurity.isManagerOfStore(#request.storeId())")
    public ProductResponse updateProduct(String productId, @Valid ProductRequest request) {
        //Fetch product object from its id
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with this id doesn't exist."));
        productMapper.toUpdatedEntity(product, request);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @PreAuthorize("@storeSecurity.isManagerOfProduct(#productId)")
    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }

    //Returns the product if the productId belongs to a storeId
    public ProductResponse getValidatedProduct(String productId, Long storeId) {
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product doesn't exist for this store."));
        return productMapper.toDTO(product);
    }
    //Returns a list of products by their ids
    public List<ProductResponse> getProductsByIds(List<String> productIds) {
        //Return a DTO list of all products by their ids
        return productRepository.findAllById(productIds).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public void decreaseStock(String productId, Integer quantity){
        //Check if the quantity is above 0
        if (quantity <= 0) throw new InsufficientException("Quantity must be greater than 0.");
        //Decrease product stock and return 1 if successful else 0
        long result = productRepository.decreaseStock(productId, quantity, -quantity);
        //Check if no updates occur to throw an error (Either quantity is invalid or productId not found)
        if (result == 0) throw new InsufficientException("Product is no longer available.");
    }

}
