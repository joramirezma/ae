package com.riwi.assesment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a task.
 */
@Schema(description = "Request payload for creating a new task")
public record CreateTaskRequest(
        @Schema(description = "Title of the task", example = "Design homepage mockup", minLength = 1, maxLength = 255)
        @NotBlank(message = "Task title is required")
        @Size(max = 255, message = "Task title cannot exceed 255 characters")
        String title
) {}
