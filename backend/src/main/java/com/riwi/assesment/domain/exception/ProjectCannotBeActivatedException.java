package com.riwi.assesment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when trying to activate a project that cannot be activated.
 * For example, when the project has no tasks or is not in DRAFT status.
 */
public class ProjectCannotBeActivatedException extends DomainException {

    public ProjectCannotBeActivatedException(UUID projectId) {
        super("Project cannot be activated: " + projectId);
    }

    public ProjectCannotBeActivatedException(String message) {
        super(message);
    }
}
