package com.Wassal.mapper;

import com.Wassal.dto.OrderAddressResponse;
import com.Wassal.model.OrderAddress;
import com.Wassal.model.UserAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderAddressMapper {
    //Maps UserAddress to OrderAddress snapshot to be used by Order model
    OrderAddress toEntity(UserAddress userAddress);

    OrderAddressResponse toDTO(OrderAddress orderAddress);
}
