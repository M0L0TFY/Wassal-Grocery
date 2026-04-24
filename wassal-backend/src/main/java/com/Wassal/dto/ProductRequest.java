package com.Wassal.dto;

import com.Wassal.model.ECategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public record ProductRequest(
        @Schema(example = "myProductName")
        @NotBlank(message = "Product name required.")
        String name,
        @Schema(example = "myProductBrand")
        String brand,
        @Schema(example = "https://myProduct-image.com")
        @NotBlank(message = "Product image URL required.")
        String imageURL,
        String description,
        @Schema(example = "BEVERAGE")
        @NotNull(message = "Product category required.")
        ECategory category,
        @Schema(example = "5.00")
        @NotNull(message = "Product price required.")
        BigDecimal price,
        @Schema(example = "100")
        @NotNull(message = "Product quantity required.")
        Integer quantity,
        Map<String, Object> attributes ,
        @Schema(example = "1")
        @NotNull(message = "Store id for this product is required.")
        Long storeId
) {
}
