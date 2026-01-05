package com.riwi.assesment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a project.
 */
@Schema(description = "Request payload for creating a new project")
public record CreateProjectRequest(
        @Schema(description = "Name of the project", example = "Website Redesign", minLength = 1, maxLength = 255)
        @NotBlank(message = "Project name is required")
        @Size(max = 255, message = "Project name cannot exceed 255 characters")
        String name
) {}
