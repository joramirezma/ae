package com.riwi.assesment.infrastructure.config;

import com.riwi.assesment.application.service.ActivateProjectService;
import com.riwi.assesment.application.service.CompleteTaskService;
import com.riwi.assesment.application.service.CreateProjectService;
import com.riwi.assesment.application.service.CreateTaskService;
import com.riwi.assesment.domain.port.in.ActivateProjectUseCase;
import com.riwi.assesment.domain.port.in.CompleteTaskUseCase;
import com.riwi.assesment.domain.port.in.CreateProjectUseCase;
import com.riwi.assesment.domain.port.in.CreateTaskUseCase;
import com.riwi.assesment.domain.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean configuration for use cases.
 * Wires the application services with their dependencies (ports).
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProjectUseCase createProjectUseCase(
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        return new CreateProjectService(
                projectRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );
    }

    @Bean
    public ActivateProjectUseCase activateProjectUseCase(
            ProjectRepositoryPort projectRepository,
            TaskRepositoryPort taskRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        return new ActivateProjectService(
                projectRepository,
                taskRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );
    }

    @Bean
    public CreateTaskUseCase createTaskUseCase(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        return new CreateTaskService(
                taskRepository,
                projectRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );
    }

    @Bean
    public CompleteTaskUseCase completeTaskUseCase(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        return new CompleteTaskService(
                taskRepository,
                projectRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );
    }
}
