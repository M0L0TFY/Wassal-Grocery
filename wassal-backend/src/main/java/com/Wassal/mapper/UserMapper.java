package com.Wassal.mapper;

import com.Wassal.dto.RegisterRequest;
import com.Wassal.dto.UserResponse;
import com.Wassal.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    //Map RegisterRequestDTO to User object
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) //Ignore password since we need to encode it first
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest request);

    UserResponse toDTO(User user);
}
