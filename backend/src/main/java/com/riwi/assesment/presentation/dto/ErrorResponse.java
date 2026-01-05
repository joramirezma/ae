package com.riwi.assesment.presentation.dto;

import java.time.LocalDateTime;

/**
 * DTO for error responses.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, LocalDateTime.now());
    }
}
