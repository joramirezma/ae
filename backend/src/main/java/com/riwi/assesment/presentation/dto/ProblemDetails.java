package com.riwi.assesment.presentation.dto;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Problem Details for HTTP APIs (RFC 7807).
 * Standard format for error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Problem Details response following RFC 7807 standard")
public record ProblemDetails(
        @Schema(description = "URI reference that identifies the problem type", example = "https://api.projectmanager.com/errors/not-found")
        URI type,
        
        @Schema(description = "Short, human-readable summary of the problem type", example = "Resource Not Found")
        String title,
        
        @Schema(description = "HTTP status code", example = "404")
        int status,
        
        @Schema(description = "Human-readable explanation specific to this occurrence", example = "Project with ID '123e4567-e89b-12d3-a456-426614174000' was not found")
        String detail,
        
        @Schema(description = "URI reference that identifies the specific occurrence of the problem", example = "/api/projects/123e4567-e89b-12d3-a456-426614174000")
        URI instance,
        
        @Schema(description = "Timestamp when the error occurred")
        Instant timestamp,
        
        @Schema(description = "Additional properties specific to the error")
        Map<String, Object> extensions
) {
    
    private static final String BASE_TYPE_URI = "https://api.projectmanager.com/errors/";
    
    /**
     * Creates a ProblemDetails for NOT_FOUND errors.
     */
    public static ProblemDetails notFound(String detail, String instance) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "not-found"),
                "Resource Not Found",
                404,
                detail,
                instance != null ? URI.create(instance) : null,
                Instant.now(),
                null
        );
    }
    
    /**
     * Creates a ProblemDetails for BAD_REQUEST errors.
     */
    public static ProblemDetails badRequest(String detail, String instance, Map<String, Object> extensions) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "bad-request"),
                "Bad Request",
                400,
                detail,
                instance != null ? URI.create(instance) : null,
                Instant.now(),
                extensions
        );
    }
    
    /**
     * Creates a ProblemDetails for VALIDATION errors.
     */
    public static ProblemDetails validation(String detail, Map<String, Object> errors) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "validation-error"),
                "Validation Failed",
                400,
                detail,
                null,
                Instant.now(),
                errors
        );
    }
    
    /**
     * Creates a ProblemDetails for FORBIDDEN errors.
     */
    public static ProblemDetails forbidden(String detail, String instance) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "forbidden"),
                "Access Denied",
                403,
                detail,
                instance != null ? URI.create(instance) : null,
                Instant.now(),
                null
        );
    }
    
    /**
     * Creates a ProblemDetails for UNAUTHORIZED errors.
     */
    public static ProblemDetails unauthorized(String detail) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "unauthorized"),
                "Authentication Required",
                401,
                detail,
                null,
                Instant.now(),
                null
        );
    }
    
    /**
     * Creates a ProblemDetails for CONFLICT errors.
     */
    public static ProblemDetails conflict(String detail, String instance) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "conflict"),
                "Resource Conflict",
                409,
                detail,
                instance != null ? URI.create(instance) : null,
                Instant.now(),
                null
        );
    }
    
    /**
     * Creates a ProblemDetails for INTERNAL_SERVER_ERROR.
     */
    public static ProblemDetails internalError(String detail, String traceId) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "internal-error"),
                "Internal Server Error",
                500,
                detail,
                null,
                Instant.now(),
                traceId != null ? Map.of("traceId", traceId) : null
        );
    }
    
    /**
     * Creates a ProblemDetails for business rule violations.
     */
    public static ProblemDetails businessRule(String title, String detail, String instance) {
        return new ProblemDetails(
                URI.create(BASE_TYPE_URI + "business-rule-violation"),
                title,
                422,
                detail,
                instance != null ? URI.create(instance) : null,
                Instant.now(),
                null
        );
    }
}
