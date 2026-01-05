package com.riwi.assesment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for authentication response.
 */
@Schema(description = "Authentication response containing JWT token")
public record AuthResponse(
        @Schema(description = "JWT token for authenticating subsequent requests", example = "eyJhbGciOiJIUzUxMiJ9...")
        String token,
        
        @Schema(description = "Authenticated username", example = "johndoe")
        String username,
        
        @Schema(description = "Result message", example = "Login successful")
        String message
) {}
