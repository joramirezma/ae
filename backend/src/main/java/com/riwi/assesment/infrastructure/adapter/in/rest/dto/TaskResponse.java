package com.riwi.assesment.infrastructure.adapter.in.rest.dto;

import com.riwi.assesment.domain.model.Task;

import java.util.UUID;

/**
 * DTO for task response.
 */
public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        boolean completed,
        boolean deleted
) {
    public static TaskResponse fromDomain(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.isCompleted(),
                task.isDeleted()
        );
    }
}
