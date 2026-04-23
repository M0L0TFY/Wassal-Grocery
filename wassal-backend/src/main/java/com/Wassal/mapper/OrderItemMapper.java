package com.Wassal.mapper;

import com.Wassal.dto.CartItemResponse;
import com.Wassal.dto.OrderItemResponse;
import com.Wassal.model.Order;
import com.Wassal.model.OrderItem;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    //Create OrderItem snapshot from CartItem
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(CartItemResponse cartItemResponse);

    @Mapping(target = "subTotal", expression = "java(orderItem.getProductPrice().multiply(java.math.BigDecimal.valueOf(orderItem.getQuantity())))")
    OrderItemResponse toDTO(OrderItem orderItem);

    List<OrderItemResponse> toDTOList(List<OrderItem> orderItems);
}
