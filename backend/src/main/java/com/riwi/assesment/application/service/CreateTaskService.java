package com.riwi.assesment.application.service;

import com.riwi.assesment.domain.exception.ProjectNotFoundException;
import com.riwi.assesment.domain.exception.UnauthorizedAccessException;
import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.CreateTaskUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;

import java.util.UUID;

/**
 * Service that implements the CreateTaskUseCase.
 * Contains the business logic for creating a new task.
 */
public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public CreateTaskService(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public Task execute(CreateTaskCommand command) {
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

        // Create new task
        Task task = Task.builder()
                .id(UUID.randomUUID())
                .projectId(projectId)
                .title(command.title())
                .completed(false)
                .deleted(false)
                .build();

        // Persist the task
        Task savedTask = taskRepository.save(task);

        // Register audit log
        auditLogPort.register("CREATE_TASK", savedTask.getId());

        // Send notification
        notificationPort.notify("Task '" + savedTask.getTitle() + "' has been created");

        return savedTask;
    }
}
