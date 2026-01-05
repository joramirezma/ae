package com.riwi.assesment.infrastructure.adapter.out.persistence.repository;

import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for TaskEntity.
 */
@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByProjectId(UUID projectId);

    List<TaskEntity> findByProjectIdAndDeletedFalse(UUID projectId);

    long countByProjectIdAndDeletedFalse(UUID projectId);

    boolean existsByProjectIdAndDeletedFalse(UUID projectId);

    boolean existsByProjectIdAndCompletedFalseAndDeletedFalse(UUID projectId);
}
