package com.riwi.assesment.domain.port.in;

import com.riwi.assesment.domain.model.Task;

import java.util.UUID;

/**
 * Input port for completing a task.
 * This interface defines the use case contract for task completion.
 */
public interface CompleteTaskUseCase {

    /**
     * Command object containing the data needed to complete a task.
     */
    record CompleteTaskCommand(UUID taskId) {
        public CompleteTaskCommand {
            if (taskId == null) {
                throw new IllegalArgumentException("Task ID cannot be null");
            }
        }
    }

    /**
     * Marks a task as completed.
     * The task must:
     * - Exist and not be deleted
     * - Belong to a project owned by the current user (ownership validation)
     * - Not be already completed
     * 
     * @param command the command containing the task ID
     * @return the completed task
     * @throws com.riwi.assesment.domain.exception.TaskNotFoundException if task doesn't exist
     * @throws com.riwi.assesment.domain.exception.UnauthorizedAccessException if user doesn't own the project
     * @throws com.riwi.assesment.domain.exception.TaskCannotBeCompletedException if task cannot be completed
     */
    Task execute(CompleteTaskCommand command);
}
