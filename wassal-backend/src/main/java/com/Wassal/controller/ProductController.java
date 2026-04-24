package com.Wassal.controller;

import com.Wassal.dto.ProductRequest;
import com.Wassal.dto.ProductResponse;
import com.Wassal.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "4. Store Products")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "GetProductsByStoreId",
            description = "Return all products associated to a store by the store id")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByStoreId(@PathVariable Long storeId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByStoreId(storeId, pageable));
    }
    @Operation(summary = "GetProductById")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Operation(summary = "CreateProduct")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @Operation(summary = "UpdateProduct")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String productId, @Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @Operation(summary = "DeleteProduct")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId){
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
