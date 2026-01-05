package com.riwi.assesment.domain.port.in;

import com.riwi.assesment.domain.model.Project;

import java.util.UUID;

/**
 * Input port for activating a project.
 * This interface defines the use case contract for project activation.
 */
public interface ActivateProjectUseCase {

    /**
     * Command object containing the data needed to activate a project.
     */
    record ActivateProjectCommand(UUID projectId) {
        public ActivateProjectCommand {
            if (projectId == null) {
                throw new IllegalArgumentException("Project ID cannot be null");
            }
        }
    }

    /**
     * Activates a project, changing its status from DRAFT to ACTIVE.
     * The project must:
     * - Exist and not be deleted
     * - Be owned by the current user (ownership validation)
     * - Be in DRAFT status
     * - Have at least one task
     * 
     * @param command the command containing the project ID
     * @return the activated project
     * @throws com.riwi.assesment.domain.exception.ProjectNotFoundException if project doesn't exist
     * @throws com.riwi.assesment.domain.exception.UnauthorizedAccessException if user doesn't own the project
     * @throws com.riwi.assesment.domain.exception.ProjectCannotBeActivatedException if project cannot be activated
     */
    Project execute(ActivateProjectCommand command);
}
