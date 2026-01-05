package com.riwi.assesment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration request.
 */
@Schema(description = "User registration request payload")
public record RegisterRequest(
        @Schema(description = "Unique username for the account", example = "johndoe", minLength = 3, maxLength = 50)
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "Valid email address", example = "john.doe@example.com", format = "email")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Password with minimum 6 characters", example = "SecurePass123!", minLength = 6)
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {}
