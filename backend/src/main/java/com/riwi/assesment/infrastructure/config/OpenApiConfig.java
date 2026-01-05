package com.riwi.assesment.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * OpenAPI/Swagger configuration.
 * Provides comprehensive API documentation with examples, schemas, and descriptions.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Project & Task Management API")
                        .version("v1.0.0")
                        .description("""
                                ## Overview
                                REST API for managing projects and tasks built with **Clean Architecture** and **Hexagonal Architecture** patterns.
                                
                                ## Features
                                - **User Authentication**: Register and login with JWT tokens
                                - **Project Management**: Create, activate, and manage projects with soft delete
                                - **Task Management**: Create tasks within projects, mark as complete
                                - **Business Rules**: Projects require tasks to be activated, tasks belong to projects
                                
                                ## Architecture
                                - **Domain Layer**: Business entities and rules
                                - **Application Layer**: Use cases and ports
                                - **Infrastructure Layer**: Persistence, security adapters
                                - **Presentation Layer**: REST controllers with DTOs
                                
                                ## Error Handling
                                All errors follow [RFC 7807](https://tools.ietf.org/html/rfc7807) Problem Details standard.
                                """)
                        .contact(new Contact()
                                .name("Riwi Assessment Team")
                                .email("assessment@riwi.com")
                                .url("https://riwi.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("http://localhost:3000").description("Frontend (proxied)")
                ))
                .tags(List.of(
                        new Tag().name("Authentication").description("User registration and login endpoints"),
                        new Tag().name("Projects").description("Project CRUD operations and lifecycle management"),
                        new Tag().name("Tasks").description("Task management within projects")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtained from /api/auth/login or /api/auth/register"))
                        .addResponses("UnauthorizedError", new ApiResponse()
                                .description("Authentication required - JWT token missing or invalid")
                                .content(new Content().addMediaType("application/problem+json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ProblemDetails")))))
                        .addResponses("ForbiddenError", new ApiResponse()
                                .description("Access denied - You don't have permission to access this resource")
                                .content(new Content().addMediaType("application/problem+json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ProblemDetails")))))
                        .addResponses("NotFoundError", new ApiResponse()
                                .description("Resource not found")
                                .content(new Content().addMediaType("application/problem+json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ProblemDetails")))))
                        .addResponses("ValidationError", new ApiResponse()
                                .description("Validation failed - Check the request body")
                                .content(new Content().addMediaType("application/problem+json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ProblemDetails")))))
                        .addResponses("BusinessRuleError", new ApiResponse()
                                .description("Business rule violation")
                                .content(new Content().addMediaType("application/problem+json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ProblemDetails")))))); 
    }
}
