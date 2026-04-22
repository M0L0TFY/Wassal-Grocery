package com.Wassal.dto;

import com.Wassal.model.ECategory;

import java.math.BigDecimal;
import java.util.Map;

public record ProductResponse(
        String id,
        String name,
        String brand,
        String imageURL,
        String description,
        ECategory category,
        BigDecimal price,
        Integer quantity,
        Map<String, Object> attributes,
        Long storeId
) {
}
