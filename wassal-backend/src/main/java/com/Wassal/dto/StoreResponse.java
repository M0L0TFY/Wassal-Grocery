package com.Wassal.dto;

import java.math.BigDecimal;

public record StoreResponse(
        Long id,
        String name,
        String logoURL,
        String location,
        BigDecimal deliveryFee
) {
}
