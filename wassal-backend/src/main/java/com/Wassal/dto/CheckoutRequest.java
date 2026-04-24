package com.Wassal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CheckoutRequest(
        @Schema(example = "1")
        Long userAddressId
) {
}
