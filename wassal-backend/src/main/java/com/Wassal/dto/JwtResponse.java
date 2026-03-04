package com.Wassal.dto;

import java.util.Set;

public record JwtResponse(
        String accessToken,
        String type,
        Long userId,
        String email,
        Set<String> roles
) {
}
