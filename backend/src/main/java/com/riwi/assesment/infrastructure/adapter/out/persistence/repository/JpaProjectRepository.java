package com.riwi.assesment.infrastructure.adapter.out.persistence.repository;

import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for ProjectEntity.
 * Note: @SQLRestriction on entity automatically filters out deleted records.
 * Use native queries with explicit deleted check to include deleted records if needed.
 */
@Repository
public interface JpaProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    /**
     * Find all projects by owner (automatically excludes deleted due to @SQLRestriction).
     */
    List<ProjectEntity> findByOwnerId(UUID ownerId);

    /**
     * Explicit method for backwards compatibility (same as findByOwnerId with @SQLRestriction).
     */
    List<ProjectEntity> findByOwnerIdAndDeletedFalse(UUID ownerId);

    /**
     * Find project including deleted ones (bypasses @SQLRestriction).
     */
    @Query(value = "SELECT * FROM projects WHERE id = :id", nativeQuery = true)
    Optional<ProjectEntity> findByIdIncludingDeleted(@Param("id") UUID id);
}
