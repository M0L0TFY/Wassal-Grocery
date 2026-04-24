package com.Wassal.controller;

import com.Wassal.dto.UserAddressRequest;
import com.Wassal.dto.UserAddressResponse;
import com.Wassal.security.UserDetailsImpl;
import com.Wassal.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/addresses")
@RequiredArgsConstructor
@Validated
@Tag(name = "5. User Address")
public class UserAddressController {
    private final UserAddressService userAddressService;

    @Operation(summary = "GetAllUserAddresses",
            description = "Return all the addresses associated to the logged-in user")
    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> getAllUserAddresses(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(userAddressService.getAllUserAddresses(user.id()));
    }

    @Operation(summary = "AddAddress")
    @PostMapping
    public ResponseEntity<UserAddressResponse> addAddress(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody UserAddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAddressService.addAddress(user.id(), request));
    }

    @Operation(summary = "UpdateAddress")
    @PutMapping("/{userAddressId}")
    public ResponseEntity<UserAddressResponse> updateAddress(@PathVariable Long userAddressId, @Valid @RequestBody UserAddressRequest request) {
        return ResponseEntity.ok(userAddressService.updateAddress(userAddressId, request));
    }

    @Operation(summary = "DeleteAddress")
    @DeleteMapping("/{userAddressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userAddressId) {
        userAddressService.deleteAddress(userAddressId);
        return ResponseEntity.noContent().build();
    }

}
