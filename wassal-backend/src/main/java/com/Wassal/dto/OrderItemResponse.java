package com.Wassal.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    String productName,
    BigDecimal productPrice,
    Integer quantity,
    BigDecimal subTotal
) {
}
