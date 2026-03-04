package com.Wassal.controller;

import com.Wassal.dto.UserResponse;
import com.Wassal.model.ERole;
import com.Wassal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/{userId}/roles/admin")
    public ResponseEntity<UserResponse> assignAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.assignRole(userId, ERole.ROLE_ADMIN));
    }

    @DeleteMapping("/{userId}/roles/admin")
    public ResponseEntity<UserResponse> revokeAdminRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.revokeRole(userId, ERole.ROLE_ADMIN));
    }

    @PutMapping("/{userId}/roles/manager")
    public ResponseEntity<UserResponse> assignManagerRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.assignRole(userId, ERole.ROLE_STORE_MANAGER));
    }

    @DeleteMapping("/{userId}/roles/manager")
    public ResponseEntity<UserResponse> revokeManagerRole(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.revokeRole(userId, ERole.ROLE_STORE_MANAGER));
    }

}
