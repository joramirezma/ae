package com.riwi.assesment.infrastructure.adapter.out.persistence.repository;

import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for ProjectEntity.
 */
@Repository
public interface JpaProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    List<ProjectEntity> findByOwnerId(UUID ownerId);

    List<ProjectEntity> findByOwnerIdAndDeletedFalse(UUID ownerId);
}
