package com.Wassal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StoreRequest(
        @Schema(example = "myStore")
        @NotBlank(message = "Store name required.")
        String name,
        @Schema(example = "https://myStore-img.com")
        @NotBlank(message = "Store logo URL required.")
        String logoURL,
        @Schema(example = "Cairo")
        @NotBlank(message = "Store location required.")
        String location,
        @Schema(example = "50")
        @NotNull(message = "Store delivery fee required.")
        BigDecimal deliveryFee
) {
}
