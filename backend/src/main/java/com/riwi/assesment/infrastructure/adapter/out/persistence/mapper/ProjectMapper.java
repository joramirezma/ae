package com.riwi.assesment.infrastructure.adapter.out.persistence.mapper;

import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.ProjectStatus;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.ProjectStatusEntity;

/**
 * Mapper to convert between Project domain model and ProjectEntity JPA entity.
 */
public class ProjectMapper {

    private ProjectMapper() {
        // Utility class
    }

    public static Project toDomain(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }
        return Project.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .name(entity.getName())
                .status(toDomainStatus(entity.getStatus()))
                .deleted(entity.isDeleted())
                .build();
    }

    public static ProjectEntity toEntity(Project domain) {
        if (domain == null) {
            return null;
        }
        return new ProjectEntity(
                domain.getId(),
                domain.getOwnerId(),
                domain.getName(),
                toEntityStatus(domain.getStatus()),
                domain.isDeleted()
        );
    }

    private static ProjectStatus toDomainStatus(ProjectStatusEntity entityStatus) {
        if (entityStatus == null) {
            return ProjectStatus.DRAFT;
        }
        return switch (entityStatus) {
            case DRAFT -> ProjectStatus.DRAFT;
            case ACTIVE -> ProjectStatus.ACTIVE;
        };
    }

    private static ProjectStatusEntity toEntityStatus(ProjectStatus domainStatus) {
        if (domainStatus == null) {
            return ProjectStatusEntity.DRAFT;
        }
        return switch (domainStatus) {
            case DRAFT -> ProjectStatusEntity.DRAFT;
            case ACTIVE -> ProjectStatusEntity.ACTIVE;
        };
    }
}
