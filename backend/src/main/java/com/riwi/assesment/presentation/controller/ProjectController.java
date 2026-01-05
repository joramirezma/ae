package com.riwi.assesment.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.assesment.domain.model.Project;
import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.ActivateProjectUseCase;
import com.riwi.assesment.domain.port.in.CreateProjectUseCase;
import com.riwi.assesment.domain.port.in.CreateTaskUseCase;
import com.riwi.assesment.domain.port.out.CurrentUserPort;
import com.riwi.assesment.domain.port.out.ProjectRepositoryPort;
import com.riwi.assesment.domain.port.out.TaskRepositoryPort;
import com.riwi.assesment.presentation.dto.CreateProjectRequest;
import com.riwi.assesment.presentation.dto.CreateTaskRequest;
import com.riwi.assesment.presentation.dto.ProblemDetails;
import com.riwi.assesment.presentation.dto.ProjectResponse;
import com.riwi.assesment.presentation.dto.TaskResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for project endpoints.
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project CRUD operations and lifecycle management. All endpoints require JWT authentication.")
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
    @Operation(
            summary = "Create a new project",
            description = "Creates a new project in PLANNING status for the authenticated user. Projects must have at least one task before they can be activated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Project",
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "name": "Website Redesign",
                                              "status": "PLANNING",
                                              "taskCount": 0,
                                              "createdAt": "2025-01-15T10:30:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - Validation failed",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetails.class))
            ),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = "application/problem+json"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Project creation details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateProjectRequest.class),
                    examples = @ExampleObject(
                            name = "Create Project",
                            value = """
                                    {
                                      "name": "Website Redesign"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        CreateProjectUseCase.CreateProjectCommand command =
                new CreateProjectUseCase.CreateProjectCommand(request.name());

        Project project = createProjectUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectResponse.fromDomain(project));
    }

    @GetMapping
    @Operation(
            summary = "Get all projects",
            description = "Retrieves all non-deleted projects owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of projects",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class)),
                            examples = @ExampleObject(
                                    name = "Project List",
                                    value = """
                                            [
                                              {
                                                "id": "550e8400-e29b-41d4-a716-446655440000",
                                                "name": "Website Redesign",
                                                "status": "ACTIVE",
                                                "taskCount": 3,
                                                "createdAt": "2025-01-15T10:30:00Z"
                                              },
                                              {
                                                "id": "660e8400-e29b-41d4-a716-446655440001",
                                                "name": "Mobile App",
                                                "status": "PLANNING",
                                                "taskCount": 0,
                                                "createdAt": "2025-01-16T09:00:00Z"
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        
        List<ProjectResponse> projects = projectRepository.findByOwnerIdAndDeletedFalse(currentUserId)
                .stream()
                .map(ProjectResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get project by ID",
            description = "Retrieves a specific project by its unique identifier. Only the owner can access their projects."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found or access denied",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetails.class))
            ),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ProjectResponse> getProject(
            @Parameter(description = "Project UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        return projectRepository.findById(id)
                .filter(project -> !project.isDeleted())
                .filter(project -> project.isOwnedBy(currentUserPort.getCurrentUserId()))
                .map(project -> ResponseEntity.ok(ProjectResponse.fromDomain(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate a project",
            description = """
                    Changes project status from PLANNING to ACTIVE.
                    
                    **Business Rule**: A project must have at least one task before it can be activated.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project activated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponse.class),
                            examples = @ExampleObject(
                                    name = "Activated Project",
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "name": "Website Redesign",
                                              "status": "ACTIVE",
                                              "taskCount": 3,
                                              "createdAt": "2025-01-15T10:30:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Project cannot be activated - No tasks or already active",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetails.class),
                            examples = @ExampleObject(
                                    name = "No Tasks Error",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Project Cannot Be Activated",
                                              "status": 400,
                                              "detail": "Project must have at least one task before activation",
                                              "instance": "/api/projects/550e8400-e29b-41d4-a716-446655440000/activate",
                                              "timestamp": "2025-01-15T10:30:00Z",
                                              "traceId": "550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ProjectResponse> activateProject(
            @Parameter(description = "Project UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        ActivateProjectUseCase.ActivateProjectCommand command =
                new ActivateProjectUseCase.ActivateProjectCommand(id);

        Project project = activateProjectUseCase.execute(command);

        return ResponseEntity.ok(ProjectResponse.fromDomain(project));
    }

    @PostMapping("/{projectId}/tasks")
    @Operation(
            summary = "Create a task for a project",
            description = "Creates a new task within a specific project. Tasks are created with PENDING status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class),
                            examples = @ExampleObject(
                                    name = "Created Task",
                                    value = """
                                            {
                                              "id": "770e8400-e29b-41d4-a716-446655440002",
                                              "title": "Design homepage mockup",
                                              "status": "PENDING",
                                              "projectId": "550e8400-e29b-41d4-a716-446655440000",
                                              "createdAt": "2025-01-15T11:00:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Task creation details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateTaskRequest.class),
                    examples = @ExampleObject(
                            name = "Create Task",
                            value = """
                                    {
                                      "title": "Design homepage mockup"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "Project UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        CreateTaskUseCase.CreateTaskCommand command =
                new CreateTaskUseCase.CreateTaskCommand(projectId, request.title());

        Task task = createTaskUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskResponse.fromDomain(task));
    }

    @GetMapping("/{projectId}/tasks")
    @Operation(
            summary = "Get all tasks for a project",
            description = "Retrieves all non-deleted tasks belonging to a specific project."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tasks",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)),
                            examples = @ExampleObject(
                                    name = "Task List",
                                    value = """
                                            [
                                              {
                                                "id": "770e8400-e29b-41d4-a716-446655440002",
                                                "title": "Design homepage mockup",
                                                "status": "COMPLETED",
                                                "projectId": "550e8400-e29b-41d4-a716-446655440000",
                                                "createdAt": "2025-01-15T11:00:00Z"
                                              },
                                              {
                                                "id": "880e8400-e29b-41d4-a716-446655440003",
                                                "title": "Implement responsive layout",
                                                "status": "PENDING",
                                                "projectId": "550e8400-e29b-41d4-a716-446655440000",
                                                "createdAt": "2025-01-15T12:00:00Z"
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<List<TaskResponse>> getProjectTasks(
            @Parameter(description = "Project UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID projectId) {
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

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a project",
            description = "Performs a soft delete on a project. The project is marked as deleted but remains in the database for audit purposes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found or access denied"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project UUID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable UUID id) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        
        return projectRepository.findById(id)
                .filter(project -> !project.isDeleted())
                .filter(project -> project.isOwnedBy(currentUserId))
                .map(project -> {
                    project.markAsDeleted();
                    projectRepository.save(project);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
