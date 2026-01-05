package com.riwi.assesment.infrastructure.adapter.out.persistence;

import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import com.riwi.assesment.infrastructure.adapter.out.persistence.mapper.ProjectMapper;
import com.riwi.assesment.infrastructure.adapter.out.persistence.repository.JpaProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence adapter implementing ProjectRepositoryPort.
 * Adapts the domain port to JPA infrastructure.
 */
@Component
public class ProjectPersistenceAdapter implements ProjectRepositoryPort {

    private final JpaProjectRepository jpaProjectRepository;

    public ProjectPersistenceAdapter(JpaProjectRepository jpaProjectRepository) {
        this.jpaProjectRepository = jpaProjectRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = ProjectMapper.toEntity(project);
        ProjectEntity savedEntity = jpaProjectRepository.save(entity);
        return ProjectMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaProjectRepository.findById(id)
                .map(ProjectMapper::toDomain);
    }

    @Override
    public List<Project> findByOwnerId(UUID ownerId) {
        return jpaProjectRepository.findByOwnerId(ownerId)
                .stream()
                .map(ProjectMapper::toDomain)
                .toList();
    }

    @Override
    public List<Project> findByOwnerIdAndDeletedFalse(UUID ownerId) {
        return jpaProjectRepository.findByOwnerIdAndDeletedFalse(ownerId)
                .stream()
                .map(ProjectMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaProjectRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaProjectRepository.deleteById(id);
    }
}
