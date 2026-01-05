package com.riwi.assesment.application.service;

import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.ProjectStatus;
import com.riwi.assesment.domain.port.in.CreateProjectUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;

import java.util.UUID;

/**
 * Service that implements the CreateProjectUseCase.
 * Contains the business logic for creating a new project.
 */
public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public CreateProjectService(
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public Project execute(CreateProjectCommand command) {
        // Get current authenticated user
        UUID currentUserId = currentUserPort.getCurrentUserId();

        // Create new project in DRAFT status
        Project project = Project.builder()
                .id(UUID.randomUUID())
                .ownerId(currentUserId)
                .name(command.name())
                .status(ProjectStatus.DRAFT)
                .deleted(false)
                .build();

        // Persist the project
        Project savedProject = projectRepository.save(project);

        // Register audit log
        auditLogPort.register("CREATE_PROJECT", savedProject.getId());

        // Send notification
        notificationPort.notify("Project '" + savedProject.getName() + "' has been created");

        return savedProject;
    }
}
