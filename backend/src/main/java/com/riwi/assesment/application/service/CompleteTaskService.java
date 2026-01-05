package com.riwi.assesment.application.service;

import java.util.UUID;

import com.riwi.assesment.domain.exception.ProjectNotFoundException;
import com.riwi.assesment.domain.exception.TaskCannotBeCompletedException;
import com.riwi.assesment.domain.exception.TaskNotFoundException;
import com.riwi.assesment.domain.exception.UnauthorizedAccessException;
import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.CompleteTaskUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;

/**
 * Service that implements the CompleteTaskUseCase.
 * Contains the business logic for completing a task.
 */
public class CompleteTaskService implements CompleteTaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public CompleteTaskService(
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
    public Task execute(CompleteTaskCommand command) {
        UUID taskId = command.taskId();
        UUID currentUserId = currentUserPort.getCurrentUserId();

        // Find task or throw exception
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        // Check if task is deleted
        if (task.isDeleted()) {
            throw new TaskNotFoundException(taskId);
        }

        // Find the project that the task belongs to
        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(task.getProjectId()));

        // Check if project is deleted
        if (project.isDeleted()) {
            throw new ProjectNotFoundException(task.getProjectId());
        }

        // Validate ownership through the project
        if (!project.isOwnedBy(currentUserId)) {
            throw new UnauthorizedAccessException(currentUserId, task.getProjectId());
        }

        // Check if task can be completed (includes project status validation)
        if (!task.canBeCompleted(project)) {
            throw new TaskCannotBeCompletedException(
                    "Task cannot be completed. It may be already completed, deleted, or the project is not active");
        }

        // Complete the task (domain logic validates project is active)
        try {
            task.complete(project);
        } catch (IllegalStateException e) {
            throw new TaskCannotBeCompletedException(e.getMessage());
        }

        // Persist the updated task
        Task savedTask = taskRepository.save(task);

        // Register audit log
        auditLogPort.register("COMPLETE_TASK", savedTask.getId());

        // Send notification
        notificationPort.notify("Task '" + savedTask.getTitle() + "' has been completed");

        return savedTask;
    }
}
