package com.riwi.assesment.infrastructure.adapter.out.persistence.mapper;

import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.TaskEntity;

/**
 * Mapper to convert between Task domain model and TaskEntity JPA entity.
 */
public class TaskMapper {

    private TaskMapper() {
        // Utility class
    }

    public static Task toDomain(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return Task.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .title(entity.getTitle())
                .completed(entity.isCompleted())
                .deleted(entity.isDeleted())
                .build();
    }

    public static TaskEntity toEntity(Task domain) {
        if (domain == null) {
            return null;
        }
        return new TaskEntity(
                domain.getId(),
                domain.getProjectId(),
                domain.getTitle(),
                domain.isCompleted(),
                domain.isDeleted()
        );
    }
}
