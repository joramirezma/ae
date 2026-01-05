package com.riwi.assesment.domain.exception;

/**
 * Base exception for all domain-related errors.
 * This is a runtime exception to avoid cluttering the code with try-catch blocks.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
