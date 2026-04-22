package com.Wassal.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long storeId,
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {
}
