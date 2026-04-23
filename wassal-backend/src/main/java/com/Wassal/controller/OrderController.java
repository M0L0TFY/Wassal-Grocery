package com.Wassal.controller;

import com.Wassal.dto.CheckoutRequest;
import com.Wassal.dto.OrderResponse;
import com.Wassal.security.UserDetailsImpl;
import com.Wassal.service.OrderService;
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
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<OrderResponse>> getAllUserOrders(
            @PathVariable Long userId,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId, pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(user.id(), pageable));
    }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStoreId(
            @PathVariable Long storeId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersByStoreId(storeId, pageable));
    }

    @PostMapping("/stores/{storeId}/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CheckoutRequest request,
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user
            ){
        return ResponseEntity.ok(orderService.checkout(request, storeId, user.id()));
    }

}
