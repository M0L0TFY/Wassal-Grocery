package com.Wassal.mapper;

import com.Wassal.dto.CartResponse;
import com.Wassal.dto.OrderResponse;
import com.Wassal.model.Order;
import com.Wassal.model.Store;
import com.Wassal.model.User;
import com.Wassal.model.UserAddress;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class, OrderAddressMapper.class })
public interface OrderMapper {
    //Map CartResponse, User and their address + Store to an Order
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING") //Set status to PENDING when creating an order
    @Mapping(target = "deliveryFee", source = "store.deliveryFee") //Set order deliveryFee as the store's deliveryFee
    @Mapping(target = "subTotal", source = "cartResponse.totalPrice") //Set subTotal of order to totalPrice of cart
    @Mapping(target = "grandTotal", expression = "java(cartResponse.totalPrice().add(store.getDeliveryFee()))") //grandTotal = subTotal + deliveryFee
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderAddress", source = "userAddress") //UserAddress is mapped to OrderAddress
    @Mapping(target = "orderItems", source = "cartResponse.items")
    @Mapping(target = "store", source = "store")
    Order toEntity(CartResponse cartResponse, UserAddress userAddress, Store store, User user);

    @Mapping(target = "totalPrice", source = "order.grandTotal")
    OrderResponse toDTO(Order order);

    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        if (order.getOrderItems() != null) {
            //Set order of each OrderItem
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }
    }
    
}
