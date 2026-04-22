package com.Wassal.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        String productId,
        String productName,
        BigDecimal productPrice,
        Integer quantity,
        BigDecimal subTotal
) {
}
