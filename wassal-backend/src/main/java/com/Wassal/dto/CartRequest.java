package com.Wassal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartRequest(
        @NotBlank(message = "Product id required.")
        String productId,
        @Schema(example = "5")
        @NotNull(message = "Product quantity required.")
        @PositiveOrZero(message = "Product quantity can't be negative.")
        Integer quantity
) {
}
