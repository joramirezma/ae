package com.riwi.assesment.infrastructure.adapter.in.rest;

import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.ActivateProjectUseCase;
import com.riwi.assesment.domain.port.in.CreateProjectUseCase;
import com.riwi.assesment.domain.port.in.CreateTaskUseCase;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;
import com.riwi.assesment.infrastructure.adapter.in.rest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for project endpoints.
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management operations")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ActivateProjectUseCase activateProjectUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final ProjectRepositoryPort projectRepository;
    private final TaskRepositoryPort taskRepository;
    private final CurrentUserPort currentUserPort;

    public ProjectController(CreateProjectUseCase createProjectUseCase,
                             ActivateProjectUseCase activateProjectUseCase,
                             CreateTaskUseCase createTaskUseCase,
                             ProjectRepositoryPort projectRepository,
                             TaskRepositoryPort taskRepository,
                             CurrentUserPort currentUserPort) {
        this.createProjectUseCase = createProjectUseCase;
        this.activateProjectUseCase = activateProjectUseCase;
        this.createTaskUseCase = createTaskUseCase;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.currentUserPort = currentUserPort;
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        CreateProjectUseCase.CreateProjectCommand command =
                new CreateProjectUseCase.CreateProjectCommand(request.name());

        Project project = createProjectUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectResponse.fromDomain(project));
    }

    @GetMapping
    @Operation(summary = "Get all projects for the current user")
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        
        List<ProjectResponse> projects = projectRepository.findByOwnerIdAndDeletedFalse(currentUserId)
                .stream()
                .map(ProjectResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID id) {
        return projectRepository.findById(id)
                .filter(project -> !project.isDeleted())
                .filter(project -> project.isOwnedBy(currentUserPort.getCurrentUserId()))
                .map(project -> ResponseEntity.ok(ProjectResponse.fromDomain(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a project")
    public ResponseEntity<ProjectResponse> activateProject(@PathVariable UUID id) {
        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(id);

        Project project = activateProjectUseCase.execute(command);

        return ResponseEntity.ok(ProjectResponse.fromDomain(project));
    }

    @PostMapping("/{projectId}/tasks")
    @Operation(summary = "Create a new task for a project")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        CreateTaskUseCase.CreateTaskCommand command =
                new CreateTaskUseCase.CreateTaskCommand(projectId, request.title());

        Task task = createTaskUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskResponse.fromDomain(task));
    }

    @GetMapping("/{projectId}/tasks")
    @Operation(summary = "Get all tasks for a project")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@PathVariable UUID projectId) {
        // Verify project ownership
        return projectRepository.findById(projectId)
                .filter(project -> !project.isDeleted())
                .filter(project -> project.isOwnedBy(currentUserPort.getCurrentUserId()))
                .map(project -> {
                    List<TaskResponse> tasks = taskRepository.findByProjectIdAndDeletedFalse(projectId)
                            .stream()
                            .map(TaskResponse::fromDomain)
                            .toList();
                    return ResponseEntity.ok(tasks);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
