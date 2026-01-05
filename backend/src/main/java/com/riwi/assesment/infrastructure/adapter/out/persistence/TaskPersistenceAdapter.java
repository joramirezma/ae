package com.riwi.assesment.infrastructure.adapter.out.persistence;

import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.TaskEntity;
import com.riwi.assesment.infrastructure.adapter.out.persistence.mapper.TaskMapper;
import com.riwi.assesment.infrastructure.adapter.out.persistence.repository.JpaTaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence adapter implementing TaskRepositoryPort.
 * Adapts the domain port to JPA infrastructure.
 */
@Component
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final JpaTaskRepository jpaTaskRepository;

    public TaskPersistenceAdapter(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        TaskEntity savedEntity = jpaTaskRepository.save(entity);
        return TaskMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaTaskRepository.findById(id)
                .map(TaskMapper::toDomain);
    }

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return jpaTaskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findByProjectIdAndDeletedFalse(UUID projectId) {
        return jpaTaskRepository.findByProjectIdAndDeletedFalse(projectId)
                .stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    public long countByProjectIdAndDeletedFalse(UUID projectId) {
        return jpaTaskRepository.countByProjectIdAndDeletedFalse(projectId);
    }

    @Override
    public boolean existsByProjectIdAndDeletedFalse(UUID projectId) {
        return jpaTaskRepository.existsByProjectIdAndDeletedFalse(projectId);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaTaskRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaTaskRepository.deleteById(id);
    }
}
