package com.Wassal.mapper;

import com.Wassal.dto.UserAddressRequest;
import com.Wassal.dto.UserAddressResponse;
import com.Wassal.model.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {
    UserAddressResponse toDTO(UserAddress userAddress);
    //Map a list of UserAddresses to UserAddressesResponse DTOs list
    List<UserAddressResponse> toDTOList(List<UserAddress> userAddresses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserAddress toEntity(UserAddressRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void toUpdatedEntity(@MappingTarget UserAddress userAddress, UserAddressRequest request);

}
