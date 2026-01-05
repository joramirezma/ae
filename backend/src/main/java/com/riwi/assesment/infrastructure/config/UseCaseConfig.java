package com.riwi.assesment.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.riwi.assesment.application.service.ActivateProjectService;
import com.riwi.assesment.application.service.CompleteTaskService;
import com.riwi.assesment.application.service.CreateProjectService;
import com.riwi.assesment.application.service.CreateTaskService;
import com.riwi.assesment.application.service.LoginUserService;
import com.riwi.assesment.application.service.RegisterUserService;
import com.riwi.assesment.domain.port.in.ActivateProjectUseCase;
import com.riwi.assesment.domain.port.in.CompleteTaskUseCase;
import com.riwi.assesment.domain.port.in.CreateProjectUseCase;
import com.riwi.assesment.domain.port.in.CreateTaskUseCase;
import com.riwi.assesment.domain.port.in.LoginUserUseCase;
import com.riwi.assesment.domain.port.in.RegisterUserUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.PasswordEncoderPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;
import com.riwi.assesment.domain.port.out.TokenProviderPort;
import com.riwi.assesment.domain.port.out.UserRepositoryPort;

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
