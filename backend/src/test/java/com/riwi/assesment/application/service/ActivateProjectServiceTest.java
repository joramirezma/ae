package com.riwi.assesment.application.service;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.riwi.assesment.domain.exception.ProjectCannotBeActivatedException;
import com.riwi.assesment.domain.exception.ProjectNotFoundException;
import com.riwi.assesment.domain.exception.UnauthorizedAccessException;
import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.ProjectStatus;
import com.riwi.assesment.domain.port.in.ActivateProjectUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;

/**
 * Unit tests for ActivateProjectService.
 * Tests are focused on business logic validation without Spring context.
 */
@ExtendWith(MockitoExtension.class)
class ActivateProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private TaskRepositoryPort taskRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private NotificationPort notificationPort;

    private ActivateProjectService activateProjectService;

    private UUID ownerId;
    private UUID projectId;
    private Project draftProject;

    @BeforeEach
    void setUp() {
        activateProjectService = new ActivateProjectService(
                projectRepository,
                taskRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );

        ownerId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        draftProject = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("Test Project")
                .status(ProjectStatus.DRAFT)
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("ActivateProject_WithTasks_ShouldSucceed")
    void activateProject_WithTasks_ShouldSucceed() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(draftProject));
        when(taskRepository.existsByProjectIdAndCompletedFalseAndDeletedFalse(projectId)).thenReturn(true);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(projectId);

        // Act
        Project result = activateProjectService.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals(ProjectStatus.ACTIVE, result.getStatus());
        assertTrue(result.isActive());

        // Verify interactions
        verify(projectRepository).findById(projectId);
        verify(taskRepository).existsByProjectIdAndCompletedFalseAndDeletedFalse(projectId);
        verify(projectRepository).save(any(Project.class));
        verify(auditLogPort).register(eq("ACTIVATE_PROJECT"), eq(projectId));
        verify(notificationPort).notify(contains("activated"));
    }

    @Test
    @DisplayName("ActivateProject_WithoutTasks_ShouldFail")
    void activateProject_WithoutTasks_ShouldFail() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(draftProject));
        when(taskRepository.existsByProjectIdAndCompletedFalseAndDeletedFalse(projectId)).thenReturn(false);

        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(projectId);

        // Act & Assert
        ProjectCannotBeActivatedException exception = assertThrows(
                ProjectCannotBeActivatedException.class,
                () -> activateProjectService.execute(command)
        );

        assertTrue(exception.getMessage().contains("at least one active"));

        // Verify that save was never called
        verify(projectRepository, never()).save(any(Project.class));
        verify(auditLogPort, never()).register(anyString(), any(UUID.class));
        verify(notificationPort, never()).notify(anyString());
    }

    @Test
    @DisplayName("ActivateProject_ByNonOwner_ShouldFail")
    void activateProject_ByNonOwner_ShouldFail() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        when(currentUserPort.getCurrentUserId()).thenReturn(differentUserId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(draftProject));

        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(projectId);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> activateProjectService.execute(command)
        );

        assertNotNull(exception);

        // Verify that task check and save were never called
        verify(taskRepository, never()).existsByProjectIdAndDeletedFalse(any(UUID.class));
        verify(projectRepository, never()).save(any(Project.class));
        verify(auditLogPort, never()).register(anyString(), any(UUID.class));
    }

    @Test
    @DisplayName("ActivateProject_ProjectNotFound_ShouldFail")
    void activateProject_ProjectNotFound_ShouldFail() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(projectId);

        // Act & Assert
        ProjectNotFoundException exception = assertThrows(
                ProjectNotFoundException.class,
                () -> activateProjectService.execute(command)
        );

        assertNotNull(exception);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("ActivateProject_AlreadyActive_ShouldFail")
    void activateProject_AlreadyActive_ShouldFail() {
        // Arrange
        Project activeProject = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("Active Project")
                .status(ProjectStatus.ACTIVE)
                .deleted(false)
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject));

        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(projectId);

        // Act & Assert
        ProjectCannotBeActivatedException exception = assertThrows(
                ProjectCannotBeActivatedException.class,
                () -> activateProjectService.execute(command)
        );

        assertTrue(exception.getMessage().contains("DRAFT") || exception.getMessage().contains("cannot be activated"));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("ActivateProject_DeletedProject_ShouldFail")
    void activateProject_DeletedProject_ShouldFail() {
        // Arrange
        Project deletedProject = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("Deleted Project")
                .status(ProjectStatus.DRAFT)
                .deleted(true)
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(deletedProject));

        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(projectId);

        // Act & Assert
        assertThrows(
                ProjectNotFoundException.class,
                () -> activateProjectService.execute(command)
        );

        verify(projectRepository, never()).save(any(Project.class));
    }
}
