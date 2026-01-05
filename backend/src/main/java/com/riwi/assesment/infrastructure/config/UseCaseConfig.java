package com.riwi.assesment.infrastructure.config;

import com.riwi.assesment.application.service.*;
import com.riwi.assesment.domain.port.in.*;
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

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider,
            AuditLogPort auditLogPort) {
        return new RegisterUserService(
                userRepository,
                passwordEncoder,
                tokenProvider,
                auditLogPort
        );
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider,
            AuditLogPort auditLogPort) {
        return new LoginUserService(
                userRepository,
                passwordEncoder,
                tokenProvider,
                auditLogPort
        );
    }
}
