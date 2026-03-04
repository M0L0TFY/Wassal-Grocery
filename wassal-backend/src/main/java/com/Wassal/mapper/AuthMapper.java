package com.Wassal.mapper;

import com.Wassal.security.UserDetailsImpl;
import com.Wassal.dto.JwtResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface AuthMapper {
    @Mapping(target = "accessToken", source = "token")
    @Mapping(target = "type", constant = "Bearer")
    @Mapping(target = "userId", source = "userDetails.id")
    @Mapping(target = "email", source = "userDetails.username")
    @Mapping(target = "roles", source = "userDetails.authorities")
    JwtResponse toJwtResponse(String token, UserDetailsImpl userDetails);
}
