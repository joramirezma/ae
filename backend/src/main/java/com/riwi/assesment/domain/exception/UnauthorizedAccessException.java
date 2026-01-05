package com.riwi.assesment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a user tries to access a resource they don't own.
 */
public class UnauthorizedAccessException extends DomainException {

    public UnauthorizedAccessException(UUID userId, UUID resourceId) {
        super("User " + userId + " is not authorized to access resource: " + resourceId);
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
