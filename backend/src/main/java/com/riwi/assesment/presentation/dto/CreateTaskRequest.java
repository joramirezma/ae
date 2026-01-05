package com.riwi.assesment.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a task.
 */
public record CreateTaskRequest(
        @NotBlank(message = "Task title is required")
        String title
) {}
