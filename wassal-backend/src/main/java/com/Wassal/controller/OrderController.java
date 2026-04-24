package com.Wassal.controller;

import com.Wassal.dto.CheckoutRequest;
import com.Wassal.dto.OrderResponse;
import com.Wassal.security.UserDetailsImpl;
import com.Wassal.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "7. Orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "GetOrderById")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(summary = "GetAllUserOrders",
            description = "Return user orders by userId")
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<OrderResponse>> getAllUserOrders(
            @PathVariable Long userId,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId, pageable));
    }

    @Operation(summary = "GetMyOrders")
    @GetMapping("/user")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(user.id(), pageable));
    }

    @Operation(summary = "GetOrdersByStoreId",
            description = "Return orders associated to a store")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStoreId(
            @PathVariable Long storeId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersByStoreId(storeId, pageable));
    }

    @Operation(summary = "Checkout",
            description = "Create an order from the shopping cart")
    @PostMapping("/stores/{storeId}/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CheckoutRequest request,
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user
            ){
        return ResponseEntity.ok(orderService.checkout(request, storeId, user.id()));
    }

}
