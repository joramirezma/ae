package com.riwi.assesment.presentation.dto;

import java.util.UUID;

import com.riwi.assesment.domain.model.Project;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for project response.
 */
@Schema(description = "Project details response")
public record ProjectResponse(
        @Schema(description = "Unique project identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        
        @Schema(description = "Owner's user identifier", example = "660e8400-e29b-41d4-a716-446655440001")
        UUID ownerId,
        
        @Schema(description = "Project name", example = "Website Redesign")
        String name,
        
        @Schema(description = "Current project status", example = "PLANNING", allowableValues = {"PLANNING", "ACTIVE", "COMPLETED"})
        String status,
        
        @Schema(description = "Whether the project is soft-deleted", example = "false")
        boolean deleted
) {
    public static ProjectResponse fromDomain(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getOwnerId(),
                project.getName(),
                project.getStatus().name(),
                project.isDeleted()
        );
    }
}
