package com.Wassal.controller;

import com.Wassal.dto.JwtResponse;
import com.Wassal.dto.LoginRequest;
import com.Wassal.dto.RegisterRequest;
import com.Wassal.dto.UserResponse;
import com.Wassal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. Auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        var loginResult = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, loginResult.refreshCookie().toString())
                .body(loginResult.jwtResponse());
    }

    @Operation(
            summary = "Refresh Token",
            description = "Endpoint to refresh token once it expires"
    )
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@CookieValue("${security.jwt.cookie-name}") String refreshToken) {
        var refreshResult = authService.refreshToken(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshResult.refreshCookie().toString())
                .body(refreshResult.jwtResponse());
    }

    @Operation(hidden = true)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("${security.jwt.cookie-name}") String refreshToken) {
        authService.logout(refreshToken);
        ResponseCookie emptyCookie = authService.getEmptyRefreshTokenCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, emptyCookie.toString())
                .build();
    }

}
