package com.Wassal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StoreRequest(
        @NotBlank(message = "Store name required.")
        String name,
        @NotBlank(message = "Store logo URL required.")
        String logoURL,
        @NotBlank(message = "Store location required.")
        String location,
        @NotNull(message = "Store delivery fee required.")
        BigDecimal deliveryFee
) {
}
