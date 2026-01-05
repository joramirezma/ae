package com.riwi.assesment.presentation.dto;

/**
 * DTO for authentication response.
 */
public record AuthResponse(
        String token,
        String username,
        String message
) {}
