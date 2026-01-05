package com.riwi.assesment.domain.port.in;

import com.riwi.assesment.domain.model.Task;

import java.util.UUID;

/**
 * Input port for creating a new task.
 * This interface defines the use case contract for task creation.
 */
public interface CreateTaskUseCase {

    /**
     * Command object containing the data needed to create a task.
     */
    record CreateTaskCommand(UUID projectId, String title) {
        public CreateTaskCommand {
            if (projectId == null) {
                throw new IllegalArgumentException("Project ID cannot be null");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Task title cannot be null or blank");
            }
        }
    }

    /**
     * Creates a new task for the specified project.
     * The task will be created in incomplete status (completed = false).
     * The project must:
     * - Exist and not be deleted
     * - Be owned by the current user (ownership validation)
     * 
     * @param command the command containing task creation data
     * @return the created task
     * @throws com.riwi.assesment.domain.exception.ProjectNotFoundException if project doesn't exist
     * @throws com.riwi.assesment.domain.exception.UnauthorizedAccessException if user doesn't own the project
     */
    Task execute(CreateTaskCommand command);
}
