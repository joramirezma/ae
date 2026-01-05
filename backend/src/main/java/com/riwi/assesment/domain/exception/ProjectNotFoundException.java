package com.riwi.assesment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested project is not found.
 */
public class ProjectNotFoundException extends DomainException {

    public ProjectNotFoundException(UUID projectId) {
        super("Project not found with id: " + projectId);
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
