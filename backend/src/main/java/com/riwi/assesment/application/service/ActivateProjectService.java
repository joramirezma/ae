package com.riwi.assesment.application.service;

import java.util.UUID;

import com.riwi.assesment.domain.exception.ProjectCannotBeActivatedException;
import com.riwi.assesment.domain.exception.ProjectNotFoundException;
import com.riwi.assesment.domain.exception.UnauthorizedAccessException;
import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.port.in.ActivateProjectUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;

/**
 * Service that implements the ActivateProjectUseCase.
 * Contains the business logic for activating a project.
 */
public class ActivateProjectService implements ActivateProjectUseCase {

    private final ProjectRepositoryPort projectRepository;
    private final TaskRepositoryPort taskRepository;
    private final CurrentUserPort currentUserPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public ActivateProjectService(
            ProjectRepositoryPort projectRepository,
            TaskRepositoryPort taskRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.currentUserPort = currentUserPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public Project execute(ActivateProjectCommand command) {
        UUID projectId = command.projectId();
        UUID currentUserId = currentUserPort.getCurrentUserId();

        // Find project or throw exception
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // Check if project is deleted
        if (project.isDeleted()) {
            throw new ProjectNotFoundException(projectId);
        }

        // Validate ownership
        if (!project.isOwnedBy(currentUserId)) {
            throw new UnauthorizedAccessException(currentUserId, projectId);
        }

        // Check if project can be activated (must be in DRAFT status)
        if (!project.canBeActivated()) {
            throw new ProjectCannotBeActivatedException(
                    "Project is not in DRAFT status. Current status: " + project.getStatus());
        }

        // Check if project has at least one active (not completed) task
        boolean hasActiveTasks = taskRepository.existsByProjectIdAndCompletedFalseAndDeletedFalse(projectId);
        if (!hasActiveTasks) {
            throw new ProjectCannotBeActivatedException(
                    "Project must have at least one active (not completed) task to be activated");
        }

        // Activate the project
        project.activate();

        // Persist the updated project
        Project savedProject = projectRepository.save(project);

        // Register audit log
        auditLogPort.register("ACTIVATE_PROJECT", savedProject.getId());

        // Send notification
        notificationPort.notify("Project '" + savedProject.getName() + "' has been activated");

        return savedProject;
    }
}
