package com.Wassal.dto;

import com.Wassal.model.EStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        EStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        OrderAddressResponse orderAddress,
        List<OrderItemResponse> orderItems
) {
}
