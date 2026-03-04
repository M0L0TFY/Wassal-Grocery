package com.Wassal.service;

import com.Wassal.dto.UserResponse;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.UserMapper;
import com.Wassal.model.ERole;
import com.Wassal.model.User;
import com.Wassal.repository.RoleRepository;
import com.Wassal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse assignRole(Long userId, ERole roleEnum) {
        //Extract user object from userId
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        //Extract the role
        var role = roleRepository.findByRole(roleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
        //Return early if the user already has the role (avoid database hit)
        if (user.getRoles().contains(role)) return userMapper.toDTO(user);
        //Add role to user
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public UserResponse revokeRole(Long userId, ERole roleEnum) {
        //Extract user object from userId
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        //Extract the role
        var role = roleRepository.findByRole(roleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
        //Check if the user has the role then remove it
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            User updatedUser = userRepository.save(user);
            return userMapper.toDTO(updatedUser);
        }
        return userMapper.toDTO(user); //Return the old user object dto if they don't have the role
    }

}
