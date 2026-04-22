package com.Wassal.controller;

import com.Wassal.dto.CartRequest;
import com.Wassal.dto.CartResponse;
import com.Wassal.security.UserDetailsImpl;
import com.Wassal.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long storeId) {
        return ResponseEntity.ok(cartService.getCart(user.id(), storeId));
    }

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long storeId,
            @Valid @RequestBody CartRequest request
            ) {
        cartService.addItemToCart(user.id(), storeId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateItemQuantity(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long storeId,
            @Valid @RequestBody CartRequest request
    ) {
        cartService.updateItemQuantity(user.id(), storeId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long storeId,
            @PathVariable String productId
    ) {
        cartService.removeItemFromCart(user.id(), storeId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long storeId) {
        cartService.clearCart(user.id(), storeId);
        return ResponseEntity.noContent().build();
    }

}
