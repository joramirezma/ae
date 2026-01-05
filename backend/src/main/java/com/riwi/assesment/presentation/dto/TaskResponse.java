package com.riwi.assesment.presentation.dto;

import java.util.UUID;

import com.riwi.assesment.domain.model.Task;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for task response.
 */
@Schema(description = "Task details response")
public record TaskResponse(
        @Schema(description = "Unique task identifier", example = "770e8400-e29b-41d4-a716-446655440002")
        UUID id,
        
        @Schema(description = "Parent project identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID projectId,
        
        @Schema(description = "Task title", example = "Design homepage mockup")
        String title,
        
        @Schema(description = "Whether the task is completed", example = "false")
        boolean completed,
        
        @Schema(description = "Whether the task is soft-deleted", example = "false")
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
