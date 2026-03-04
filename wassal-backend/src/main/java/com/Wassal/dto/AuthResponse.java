package com.Wassal.dto;

import org.springframework.http.ResponseCookie;

public record AuthResponse(
        JwtResponse jwtResponse,
        ResponseCookie refreshCookie
) {
}
