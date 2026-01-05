package com.riwi.assesment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when trying to complete a task that cannot be completed.
 * For example, when the task is already completed or deleted.
 */
public class TaskCannotBeCompletedException extends DomainException {

    public TaskCannotBeCompletedException(UUID taskId) {
        super("Task cannot be completed: " + taskId);
    }

    public TaskCannotBeCompletedException(String message) {
        super(message);
    }
}
