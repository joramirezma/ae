package com.riwi.assesment.domain.port.out;

import com.riwi.assesment.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Project persistence operations.
 * This interface defines the contract that any persistence adapter must implement.
 */
public interface ProjectRepositoryPort {

    /**
     * Saves a project to the database.
     * @param project the project to save
     * @return the saved project with generated ID if new
     */
    Project save(Project project);

    /**
     * Finds a project by its ID.
     * @param id the project ID
     * @return an Optional containing the project if found, empty otherwise
     */
    Optional<Project> findById(UUID id);

    /**
     * Finds all projects owned by a specific user.
     * @param ownerId the owner's user ID
     * @return list of projects owned by the user
     */
    List<Project> findByOwnerId(UUID ownerId);

    /**
     * Finds all non-deleted projects owned by a specific user.
     * @param ownerId the owner's user ID
     * @return list of active (non-deleted) projects owned by the user
     */
    List<Project> findByOwnerIdAndDeletedFalse(UUID ownerId);

    /**
     * Checks if a project exists by ID.
     * @param id the project ID
     * @return true if the project exists, false otherwise
     */
    boolean existsById(UUID id);

    /**
     * Deletes a project by its ID (hard delete).
     * Note: Prefer soft delete using markAsDeleted() method in the entity.
     * @param id the project ID
     */
    void deleteById(UUID id);
}
