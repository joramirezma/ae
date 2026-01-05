package com.riwi.assesment.infrastructure.adapter.in.rest.dto;

/**
 * DTO for authentication response.
 */
public record AuthResponse(
        String token,
        String username,
        String message
) {}
