package com.riwi.assesment.infrastructure.adapter.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.TaskEntity;

/**
 * Spring Data JPA Repository for TaskEntity.
 * Note: @SQLRestriction on entity automatically filters out deleted records.
 * Use native queries with explicit deleted check to include deleted records if needed.
 */
@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID> {

    /**
     * Find all tasks by project (automatically excludes deleted due to @SQLRestriction).
     */
    List<TaskEntity> findByProjectId(UUID projectId);

    /**
     * Explicit method for backwards compatibility (same as findByProjectId with @SQLRestriction).
     */
    List<TaskEntity> findByProjectIdAndDeletedFalse(UUID projectId);

    /**
     * Count non-deleted tasks by project (automatically filtered by @SQLRestriction).
     */
    long countByProjectId(UUID projectId);

    /**
     * Explicit count for backwards compatibility.
     */
    long countByProjectIdAndDeletedFalse(UUID projectId);

    /**
     * Check if any non-deleted task exists for project.
     */
    boolean existsByProjectId(UUID projectId);

    /**
     * Explicit method for backwards compatibility.
     */
    boolean existsByProjectIdAndDeletedFalse(UUID projectId);

    /**
     * Check if any active (not completed and not deleted) task exists for project.
     * The @SQLRestriction handles deleted=false, so we only need to check completed=false.
     */
    boolean existsByProjectIdAndCompletedFalse(UUID projectId);

    /**
     * Explicit method for backwards compatibility.
     */
    boolean existsByProjectIdAndCompletedFalseAndDeletedFalse(UUID projectId);

    /**
     * Find task including deleted ones (bypasses @SQLRestriction).
     */
    @Query(value = "SELECT * FROM tasks WHERE id = :id", nativeQuery = true)
    Optional<TaskEntity> findByIdIncludingDeleted(@Param("id") UUID id);

    /**
     * Find all tasks for projects owned by a specific user.
     */
    @Query("SELECT t FROM TaskEntity t JOIN ProjectEntity p ON t.projectId = p.id WHERE p.ownerId = :ownerId")
    List<TaskEntity> findAllByProjectOwnerId(@Param("ownerId") UUID ownerId);
}
