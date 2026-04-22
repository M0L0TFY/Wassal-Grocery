package com.Wassal.mapper;

import com.Wassal.dto.CartItemResponse;
import com.Wassal.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "subTotal", expression = "java(product.price().multiply(java.math.BigDecimal.valueOf(quantity)))") //subTotal = productPrice x quantity
    CartItemResponse toCartItemResponse(ProductResponse product, Integer quantity);
}
