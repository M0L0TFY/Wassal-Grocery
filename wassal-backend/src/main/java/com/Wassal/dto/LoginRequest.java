package com.Wassal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email required.") @Email
        String email,
        @NotBlank(message = "Password required.") @Size(min = 6, message = "Password must contain at least 6 characters.")
        String password
) {
}
