package com.Wassal.controller;

import com.Wassal.dto.UserResponse;
import com.Wassal.model.ERole;
import com.Wassal.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "2. Admin Methods")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "AssignAdminRole",
            description = "Assign admin role to a user")
    @PutMapping("/{userId}/roles/admin")
    public ResponseEntity<UserResponse> assignAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.assignRole(userId, ERole.ROLE_ADMIN));
    }

    @Operation(summary = "RevokeAdminRole",
            description = "Remove admin role from a user")
    @DeleteMapping("/{userId}/roles/admin")
    public ResponseEntity<UserResponse> revokeAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.revokeRole(userId, ERole.ROLE_ADMIN));
    }

    @Operation(summary = "AssignManagerRole",
            description = "Assign manager role to a user")
    @PutMapping("/{userId}/roles/manager")
    public ResponseEntity<UserResponse> assignManagerRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.assignRole(userId, ERole.ROLE_STORE_MANAGER));
    }

    @Operation(summary = "RevokeManagerRole",
            description = "Remove manager role from a user")
    @DeleteMapping("/{userId}/roles/manager")
    public ResponseEntity<UserResponse> revokeManagerRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.revokeRole(userId, ERole.ROLE_STORE_MANAGER));
    }

}
