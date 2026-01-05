package com.riwi.assesment.domain.exception;

/**
 * Exception thrown when attempting to register a user that already exists.
 */
public class UserAlreadyExistsException extends DomainException {
    
    public UserAlreadyExistsException(String field, String value) {
        super(String.format("User with %s '%s' already exists", field, value));
    }
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
