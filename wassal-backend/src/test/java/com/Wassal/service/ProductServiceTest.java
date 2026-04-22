package com.Wassal.service;

import com.Wassal.dto.ProductRequest;
import com.Wassal.dto.ProductResponse;
import com.Wassal.exception.AlreadyExistsException;
import com.Wassal.exception.InsufficientException;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.ProductMapper;
import com.Wassal.model.ECategory;
import com.Wassal.model.Product;
import com.Wassal.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Get Products Page By Store ID Tests")
    void getProductsByStoreIdSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Product productEntity = productBuilder("mongo-id-123");
        ProductResponse expectedResponse = productResponseBuilder();

        Page<Product> productsPage = new PageImpl<>(List.of(productEntity), pageable, 1);
        when(productRepository.findAllByStoreId(1L, pageable)).thenReturn(productsPage);
        when(productMapper.toDTO(productEntity)).thenReturn(expectedResponse);

        Page<ProductResponse> actualPage = productService.getProductsByStoreId(1L, pageable);
        assertNotNull(actualPage);

        verify(productRepository, times(1)).findAllByStoreId(1L, pageable);
        verify(productMapper, times(1)).toDTO(productEntity);
    }

    @Nested
    class GetProductById {
        @Test
        void getProductByIdSuccess() {
            Product productEntity = productBuilder("mongo-id-123");
            ProductResponse expectedResponse = productResponseBuilder();

            when(productRepository.findById("mongo-id-123")).thenReturn(Optional.of(productEntity));
            when(productMapper.toDTO(productEntity)).thenReturn(expectedResponse);

            ProductResponse actualResponse = productService.getProductById("mongo-id-123");
            assertNotNull(actualResponse);
            assertEquals("mongo-id-123", actualResponse.id());

            verify(productRepository, times(1)).findById("mongo-id-123");
            verify(productMapper, times(1)).toDTO(productEntity);
        }

        @Test
        void productNotFound() {
            when(productRepository.findById("wrong-id-123")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, ()-> {
                productService.getProductById("wrong-id-123");
            });
            verify(productMapper, never()).toDTO((Product) any());
        }
    }

    @Nested
    class CreateProduct{
        @Test
        void createProductSuccess() {
            ProductRequest request = productRequestBuilder();
            Product productEntity = productBuilder(null);
            Product savedEntity = productBuilder("mongo-id-123");
            ProductResponse expectedResponse = productResponseBuilder();

            when(productRepository.existsByStoreIdAndNameAndBrand(request.storeId(), request.name(), request.brand())).thenReturn(false);
            when(productMapper.toEntity(request)).thenReturn(productEntity);
            when(productRepository.save(productEntity)).thenReturn(savedEntity);
            when(productMapper.toDTO(savedEntity)).thenReturn(expectedResponse);

            ProductResponse actualResponse = productService.createProduct(request);
            assertNotNull(actualResponse);
            assertEquals("mongo-id-123", actualResponse.id());

            verify(productMapper, times(1)).toEntity(request);
            verify(productRepository, times(1)).save(productEntity);
        }

        @Test
        void duplicateProductShouldFail() {
            ProductRequest request = productRequestBuilder();
            when(productRepository.existsByStoreIdAndNameAndBrand(request.storeId(), request.name(), request.brand())).thenReturn(true);

            assertThrows(AlreadyExistsException.class, () -> {
                productService.createProduct(request);
            });
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateProduct {
        @Test
        void updateProductSuccess() {
            ProductRequest request = productRequestBuilder();
            Product productEntity = productBuilder("mongo-id-123"); //old product
            Product updatedEntity = productBuilder("mongo-id-123"); //updated product
            ProductResponse expectedResponse = productResponseBuilder();

            when(productRepository.findById("mongo-id-123")).thenReturn(Optional.of(productEntity));
            doNothing().when(productMapper).toUpdatedEntity(productEntity, request);
            when(productRepository.save(productEntity)).thenReturn(updatedEntity);
            when(productMapper.toDTO(updatedEntity)).thenReturn(expectedResponse);

            ProductResponse actualResponse = productService.updateProduct("mongo-id-123", request);
            assertNotNull(actualResponse);
            assertEquals("mongo-id-123", actualResponse.id());

            verify(productMapper, times(1)).toUpdatedEntity(productEntity, request);
            verify(productRepository, times(1)).save(productEntity);
        }

        @Test
        void productNotFound() {
            ProductRequest request = productRequestBuilder();
            when(productRepository.findById("wrong-id-123")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, ()-> {
                productService.updateProduct("wrong-id-123", request);
            });
            verify(productMapper, never()).toUpdatedEntity(any(), any());
            verify(productRepository, never()).save(any());
        }
    }

    @Test
    void deleteProductSuccess() {
        productService.deleteProduct("mongo-id-123");
        verify(productRepository, times(1)).deleteById("mongo-id-123");
    }

    @Nested
    class DecreaseStock {
        @Test
        void decreaseStockSuccess() {
            when(productRepository.decreaseStock("mongo-id-123", 1, -1)).thenReturn(1L);
            assertDoesNotThrow(()->{
                productService.decreaseStock("mongo-id-123", 1);
            });
            verify(productRepository, times(1)).decreaseStock("mongo-id-123", 1, -1);
        }

        @Test
        void productOutOfStock() {
            when(productRepository.decreaseStock("mongo-id-123", 1, -1)).thenReturn(0L);
            assertThrows(InsufficientException.class, ()->{
                productService.decreaseStock("mongo-id-123", 1);
            });
            verify(productRepository, times(1)).decreaseStock("mongo-id-123", 1, -1);
        }

        @Test
        void decreaseStockInvalidQuantity() {
            assertThrows(InsufficientException.class, ()->{
                productService.decreaseStock("mongo-id-123", 0);
            });
            verify(productRepository, never()).decreaseStock(anyString(), anyInt(), anyInt());
        }
    }




    private ProductRequest productRequestBuilder(){
        return new ProductRequest(
                "Name",
                "Brand",
                "http://image.com",
                "test",
                ECategory.DAIRY,
                BigDecimal.TEN,
                10,
                new HashMap<>(),
                1L
        );
    }

    private Product productBuilder(String id){
        return Product.builder()
                .id(id)
                .name("Name")
                .brand("Brand")
                .imageURL("http://image.com")
                .description("test")
                .category(ECategory.DAIRY)
                .price(BigDecimal.TEN)
                .quantity(10)
                .attributes(new HashMap<>())
                .createdAt(new Date().toInstant())
                .storeId(1L)
                .build();
    }

    private ProductResponse productResponseBuilder(){
        return new ProductResponse(
                "mongo-id-123",
                "Name",
                "Brand",
                "http://image.com",
                "test",
                ECategory.DAIRY,
                BigDecimal.TEN,
                10,
                new HashMap<>(),
                1L
        );
    }

}