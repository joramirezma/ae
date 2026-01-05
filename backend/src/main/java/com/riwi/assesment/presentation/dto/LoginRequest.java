package com.riwi.assesment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login request.
 */
@Schema(description = "User login credentials")
public record LoginRequest(
        @Schema(description = "Username of the registered account", example = "johndoe")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Account password", example = "SecurePass123!")
        @NotBlank(message = "Password is required")
        String password
) {}
