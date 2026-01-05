package com.riwi.assesment.application.service;

import java.util.Optional;
import java.util.UUID;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.riwi.assesment.domain.exception.ProjectNotFoundException;
import com.riwi.assesment.domain.exception.TaskCannotBeCompletedException;
import com.riwi.assesment.domain.exception.TaskNotFoundException;
import com.riwi.assesment.domain.exception.UnauthorizedAccessException;
import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.ProjectStatus;
import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.CompleteTaskUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.NotificationPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;

/**
 * Unit tests for CompleteTaskService.
 * Tests are focused on business logic validation without Spring context.
 */
@ExtendWith(MockitoExtension.class)
class CompleteTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepository;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private NotificationPort notificationPort;

    private CompleteTaskService completeTaskService;

    private UUID ownerId;
    private UUID projectId;
    private UUID taskId;
    private Project project;
    private Task incompleteTask;

    @BeforeEach
    void setUp() {
        completeTaskService = new CompleteTaskService(
                taskRepository,
                projectRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );

        ownerId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        project = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("Test Project")
                .status(ProjectStatus.ACTIVE)
                .deleted(false)
                .build();

        incompleteTask = Task.builder()
                .id(taskId)
                .projectId(projectId)
                .title("Test Task")
                .completed(false)
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("CompleteTask_ShouldSucceed")
    void completeTask_ShouldSucceed() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(incompleteTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act
        Task result = completeTaskService.execute(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.isCompleted());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(projectRepository).findById(projectId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("CompleteTask_ShouldGenerateAuditAndNotification")
    void completeTask_ShouldGenerateAuditAndNotification() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(incompleteTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act
        completeTaskService.execute(command);

        // Assert - Verify audit log was registered
        verify(auditLogPort, times(1)).register(eq("COMPLETE_TASK"), eq(taskId));

        // Assert - Verify notification was sent
        verify(notificationPort, times(1)).notify(contains("completed"));
    }

    @Test
    @DisplayName("CompleteTask_AlreadyCompleted_ShouldFail")
    void completeTask_AlreadyCompleted_ShouldFail() {
        // Arrange
        Task completedTask = Task.builder()
                .id(taskId)
                .projectId(projectId)
                .title("Completed Task")
                .completed(true)
                .deleted(false)
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(completedTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act & Assert
        TaskCannotBeCompletedException exception = assertThrows(
                TaskCannotBeCompletedException.class,
                () -> completeTaskService.execute(command)
        );

        assertNotNull(exception);

        // Verify that save was never called
        verify(taskRepository, never()).save(any(Task.class));
        verify(auditLogPort, never()).register(anyString(), any(UUID.class));
        verify(notificationPort, never()).notify(anyString());
    }

    @Test
    @DisplayName("CompleteTask_TaskNotFound_ShouldFail")
    void completeTask_TaskNotFound_ShouldFail() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> completeTaskService.execute(command)
        );

        assertNotNull(exception);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("CompleteTask_ByNonOwner_ShouldFail")
    void completeTask_ByNonOwner_ShouldFail() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        when(currentUserPort.getCurrentUserId()).thenReturn(differentUserId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(incompleteTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> completeTaskService.execute(command)
        );

        assertNotNull(exception);

        // Verify that save was never called
        verify(taskRepository, never()).save(any(Task.class));
        verify(auditLogPort, never()).register(anyString(), any(UUID.class));
    }

    @Test
    @DisplayName("CompleteTask_DeletedTask_ShouldFail")
    void completeTask_DeletedTask_ShouldFail() {
        // Arrange
        Task deletedTask = Task.builder()
                .id(taskId)
                .projectId(projectId)
                .title("Deleted Task")
                .completed(false)
                .deleted(true)
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(deletedTask));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act & Assert
        assertThrows(
                TaskNotFoundException.class,
                () -> completeTaskService.execute(command)
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("CompleteTask_DeletedProject_ShouldFail")
    void completeTask_DeletedProject_ShouldFail() {
        // Arrange
        Project deletedProject = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("Deleted Project")
                .status(ProjectStatus.ACTIVE)
                .deleted(true)
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(incompleteTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(deletedProject));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act & Assert
        assertThrows(
                ProjectNotFoundException.class,
                () -> completeTaskService.execute(command)
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("CompleteTask_ProjectInDraft_ShouldFail")
    void completeTask_ProjectInDraft_ShouldFail() {
        // Arrange - Project in DRAFT status (not ACTIVE)
        Project draftProject = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name("Draft Project")
                .status(ProjectStatus.DRAFT)
                .deleted(false)
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(ownerId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(incompleteTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(draftProject));

        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(taskId);

        // Act & Assert - Cannot complete tasks in DRAFT projects
        TaskCannotBeCompletedException exception = assertThrows(
                TaskCannotBeCompletedException.class,
                () -> completeTaskService.execute(command)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("ACTIVE") || exception.getMessage().contains("not active"));

        // Verify that save was never called
        verify(taskRepository, never()).save(any(Task.class));
        verify(auditLogPort, never()).register(anyString(), any(UUID.class));
        verify(notificationPort, never()).notify(anyString());
    }
}
