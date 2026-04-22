package com.Wassal.dto;

import com.Wassal.model.ECategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public record ProductRequest(
        @NotBlank(message = "Product name required.")
        String name,
        String brand,
        @NotBlank(message = "Product image URL required.")
        String imageURL,
        String description,
        @NotNull(message = "Product category required.")
        ECategory category,
        @NotNull(message = "Product price required.")
        BigDecimal price,
        @NotNull(message = "Product quantity required.")
        Integer quantity,
        Map<String, Object> attributes ,
        @NotNull(message = "Store id for this product is required.")
        Long storeId
) {
}
