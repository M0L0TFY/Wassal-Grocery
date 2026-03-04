package com.Wassal.dto;

import java.math.BigDecimal;

public record DetailedStoreResponse(
        Long id,
        String name,
        String logoURL,
        String location,
        BigDecimal deliveryFee,
        ManagerResponse manager
) {
}
