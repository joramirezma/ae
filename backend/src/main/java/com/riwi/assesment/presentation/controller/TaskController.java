package com.riwi.assesment.presentation.controller;

import com.riwi.assesment.domain.model.Task;
import com.riwi.assesment.domain.port.in.CompleteTaskUseCase;
import com.riwi.assesment.presentation.dto.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for task endpoints.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management operations")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final CompleteTaskUseCase completeTaskUseCase;

    public TaskController(CompleteTaskUseCase completeTaskUseCase) {
        this.completeTaskUseCase = completeTaskUseCase;
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a task as completed")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable UUID id) {
        CompleteTaskUseCase.CompleteTaskCommand command =
                new CompleteTaskUseCase.CompleteTaskCommand(id);

        Task task = completeTaskUseCase.execute(command);

        return ResponseEntity.ok(TaskResponse.fromDomain(task));
    }
}
