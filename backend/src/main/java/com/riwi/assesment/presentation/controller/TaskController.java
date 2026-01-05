package com.riwi.assesment.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.CompleteTaskUseCase;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;
import com.riwi.assesment.presentation.dto.ProblemDetails;
import com.riwi.assesment.presentation.dto.TaskResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for task endpoints.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task lifecycle management. Tasks belong to projects and can be marked as complete. All endpoints require JWT authentication.")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final CompleteTaskUseCase completeTaskUseCase;
    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;

    public TaskController(CompleteTaskUseCase completeTaskUseCase,
                          TaskRepositoryPort taskRepository,
                          ProjectRepositoryPort projectRepository,
                          CurrentUserPort currentUserPort) {
        this.completeTaskUseCase = completeTaskUseCase;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
    }

    @PatchMapping("/{id}/complete")
    @Operation(
            summary = "Mark task as completed",
            description = """
                    Changes task status from PENDING to COMPLETED.
                    
                    **Business Rule**: A task can only be completed if it's currently in PENDING status and belongs to a project owned by the authenticated user.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task marked as completed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class),
                            examples = @ExampleObject(
                                    name = "Completed Task",
                                    value = """
                                            {
                                              "id": "770e8400-e29b-41d4-a716-446655440002",
                                              "title": "Design homepage mockup",
                                              "status": "COMPLETED",
                                              "projectId": "550e8400-e29b-41d4-a716-446655440000",
                                              "createdAt": "2025-01-15T11:00:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task cannot be completed - Already completed or invalid state",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetails.class),
                            examples = @ExampleObject(
                                    name = "Already Completed",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Task Cannot Be Completed",
                                              "status": 400,
                                              "detail": "Task is already completed",
                                              "instance": "/api/tasks/770e8400-e29b-41d4-a716-446655440002/complete",
                                              "timestamp": "2025-01-15T12:00:00Z",
                                              "traceId": "550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - Task belongs to another user's project")
    })
    public ResponseEntity<TaskResponse> completeTask(
            @Parameter(description = "Task UUID", example = "770e8400-e29b-41d4-a716-446655440002", required = true)
            @PathVariable UUID id) {
        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(id);

        Task task = completeTaskUseCase.execute(command);

        return ResponseEntity.ok(TaskResponse.fromDomain(task));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a task",
            description = "Performs a soft delete on a task. The task is marked as deleted but remains in the database for audit purposes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found or access denied"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task UUID", example = "770e8400-e29b-41d4-a716-446655440002", required = true)
            @PathVariable UUID id) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        
        return taskRepository.findById(id)
                .filter(task -> !task.isDeleted())
                .flatMap(task -> projectRepository.findById(task.getProjectId())
                        .filter(project -> project.isOwnedBy(currentUserId))
                        .map(project -> {
                            task.markAsDeleted();
                            taskRepository.save(task);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElse(ResponseEntity.notFound().build());
    }
}
