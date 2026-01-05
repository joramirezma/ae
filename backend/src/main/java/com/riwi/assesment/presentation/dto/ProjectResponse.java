package com.riwi.assesment.presentation.dto;

import com.riwi.assesment.domain.model.Project;

import java.util.UUID;

/**
 * DTO for project response.
 */
public record ProjectResponse(
        UUID id,
        UUID ownerId,
        String name,
        String status,
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
