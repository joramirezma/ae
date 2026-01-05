package com.riwi.assesment.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a project.
 */
public record CreateProjectRequest(
        @NotBlank(message = "Project name is required")
        String name
) {}
