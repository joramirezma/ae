package com.riwi.assesment.presentation.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.riwi.assesment.domain.exception.InvalidCredentialsException;
import com.riwi.assesment.domain.exception.ProjectCannotBeActivatedException;
import com.riwi.assesment.domain.exception.ProjectNotFoundException;
import com.riwi.assesment.domain.exception.TaskCannotBeCompletedException;
import com.riwi.assesment.domain.exception.TaskNotFoundException;
import com.riwi.assesment.domain.exception.UnauthorizedAccessException;
import com.riwi.assesment.domain.exception.UserAlreadyExistsException;
import com.riwi.assesment.presentation.dto.ProblemDetails;

/**
 * Global exception handler for REST controllers.
 * Implements RFC 7807 Problem Details standard for error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_JSON = "application/problem+json";

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleProjectNotFound(ProjectNotFoundException ex, WebRequest request) {
        log.warn("Project not found: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.notFound(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleTaskNotFound(TaskNotFoundException ex, WebRequest request) {
        log.warn("Task not found: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.notFound(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ProblemDetails> handleUnauthorizedAccess(UnauthorizedAccessException ex, WebRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.forbidden(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(ProjectCannotBeActivatedException.class)
    public ResponseEntity<ProblemDetails> handleProjectCannotBeActivated(ProjectCannotBeActivatedException ex, WebRequest request) {
        log.info("Project activation failed: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.businessRule(
                "Project Cannot Be Activated",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(TaskCannotBeCompletedException.class)
    public ResponseEntity<ProblemDetails> handleTaskCannotBeCompleted(TaskCannotBeCompletedException ex, WebRequest request) {
        log.info("Task completion failed: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.businessRule(
                "Task Cannot Be Completed",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetails> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) {
        log.info("User registration conflict: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.conflict(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetails> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials attempt");
        ProblemDetails problem = ProblemDetails.unauthorized(
                "Invalid username or password. Please check your credentials and try again."
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetails> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.badRequest(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetails> handleIllegalState(IllegalStateException ex, WebRequest request) {
        log.warn("Invalid state: {}", ex.getMessage());
        ProblemDetails problem = ProblemDetails.badRequest(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.info("Validation failed: {}", ex.getMessage());
        
        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        ProblemDetails problem = ProblemDetails.validation(
                "One or more fields failed validation. Please check the 'extensions' field for details.",
                Map.of("fieldErrors", fieldErrors)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGenericException(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unexpected error [traceId={}]: {}", traceId, ex.getMessage(), ex);
        
        ProblemDetails problem = ProblemDetails.internalError(
                "An unexpected error occurred. Please contact support with trace ID: " + traceId,
                traceId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType(PROBLEM_JSON))
                .body(problem);
    }
}
