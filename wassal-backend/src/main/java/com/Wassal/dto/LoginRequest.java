package com.Wassal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(example = "owner@wassal.com")
        @NotBlank(message = "Email required.") @Email
        String email,
        @Schema(example = "admin123")
        @NotBlank(message = "Password required.") @Size(min = 6, message = "Password must contain at least 6 characters.")
        String password
) {
}
