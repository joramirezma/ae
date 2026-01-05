package com.riwi.assesment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested task is not found.
 */
public class TaskNotFoundException extends DomainException {

    public TaskNotFoundException(UUID taskId) {
        super("Task not found with id: " + taskId);
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
