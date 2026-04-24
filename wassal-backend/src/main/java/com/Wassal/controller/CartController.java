package com.Wassal.controller;

import com.Wassal.dto.CartRequest;
import com.Wassal.dto.CartResponse;
import com.Wassal.security.UserDetailsImpl;
import com.Wassal.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "6. Shopping Cart")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "GetCart")
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long storeId) {
        return ResponseEntity.ok(cartService.getCart(user.id(), storeId));
    }

    @Operation(summary = "AddItemToCart")
    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long storeId,
            @Valid @RequestBody CartRequest request
            ) {
        cartService.addItemToCart(user.id(), storeId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "UpdateItemQuantityInCart")
    @PutMapping
    public ResponseEntity<Void> updateItemQuantity(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long storeId,
            @Valid @RequestBody CartRequest request
    ) {
        cartService.updateItemQuantity(user.id(), storeId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "RemoveItemFromCart")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long storeId,
            @PathVariable String productId
    ) {
        cartService.removeItemFromCart(user.id(), storeId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "ClearCart")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long storeId) {
        cartService.clearCart(user.id(), storeId);
        return ResponseEntity.noContent().build();
    }

}
