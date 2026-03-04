package com.Wassal.mapper;

import com.Wassal.model.Role;
import org.mapstruct.Mapper;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    //default: used to customize mapstruct
    //UserDetails Authorities to Set of Strings
    default Set<String> authoritiesToStrings(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) return Set.of();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    //Role object to String
    default String roleToString(Role role) {
        if (role == null) return null;
        return role.getRole().name();
    }

    //Set of Role objects to Set of Strings
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(this::roleToString)
                .collect(Collectors.toSet());
    }
}
