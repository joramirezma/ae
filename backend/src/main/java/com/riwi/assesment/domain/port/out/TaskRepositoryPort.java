package com.riwi.assesment.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.riwi.assesment.domain.model.Task;

/**
 * Output port for Task persistence operations.
 * This interface defines the contract that any persistence adapter must implement.
 */
public interface TaskRepositoryPort {

    /**
     * Saves a task to the database.
     * @param task the task to save
     * @return the saved task with generated ID if new
     */
    Task save(Task task);

    /**
     * Finds a task by its ID.
     * @param id the task ID
     * @return an Optional containing the task if found, empty otherwise
     */
    Optional<Task> findById(UUID id);

    /**
     * Finds all tasks belonging to a specific project.
     * @param projectId the project ID
     * @return list of tasks belonging to the project
     */
    List<Task> findByProjectId(UUID projectId);

    /**
     * Finds all non-deleted tasks belonging to a specific project.
     * @param projectId the project ID
     * @return list of active (non-deleted) tasks belonging to the project
     */
    List<Task> findByProjectIdAndDeletedFalse(UUID projectId);

    /**
     * Counts the number of non-deleted tasks in a project.
     * @param projectId the project ID
     * @return the count of active tasks
     */
    long countByProjectIdAndDeletedFalse(UUID projectId);

    /**
     * Checks if a project has any tasks.
     * @param projectId the project ID
     * @return true if the project has at least one task, false otherwise
     */
    boolean existsByProjectIdAndDeletedFalse(UUID projectId);

    /**
     * Checks if a project has any active (not completed and not deleted) tasks.
     * @param projectId the project ID
     * @return true if the project has at least one active task, false otherwise
     */
    boolean existsByProjectIdAndCompletedFalseAndDeletedFalse(UUID projectId);

    /**
     * Checks if a task exists by ID.
     * @param id the task ID
     * @return true if the task exists, false otherwise
     */
    boolean existsById(UUID id);

    /**
     * Finds all non-deleted tasks for projects owned by a specific user.
     * @param ownerId the owner user ID
     * @return list of tasks from projects owned by the user
     */
    List<Task> findAllByProjectOwnerId(UUID ownerId);

    /**
     * Deletes a task by its ID (hard delete).
     * Note: Prefer soft delete using markAsDeleted() method in the entity.
     * @param id the task ID
     */
    void deleteById(UUID id);
}
