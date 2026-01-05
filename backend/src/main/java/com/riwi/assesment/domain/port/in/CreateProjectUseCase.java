package com.riwi.assesment.domain.port.in;

import com.riwi.assesment.domain.model.Project;

/**
 * Input port for creating a new project.
 * This interface defines the use case contract for project creation.
 */
public interface CreateProjectUseCase {

    /**
     * Command object containing the data needed to create a project.
     */
    record CreateProjectCommand(String name) {
        public CreateProjectCommand {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Project name cannot be null or blank");
            }
        }
    }

    /**
     * Creates a new project for the current authenticated user.
     * The project will be created in DRAFT status.
     * @param command the command containing project creation data
     * @return the created project
     */
    Project execute(CreateProjectCommand command);
}
