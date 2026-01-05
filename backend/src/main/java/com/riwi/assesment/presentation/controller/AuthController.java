package com.riwi.assesment.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.assesment.domain.port.in.LoginUserUseCase;
import com.riwi.assesment.domain.port.in.RegisterUserUseCase;
import com.riwi.assesment.presentation.dto.AuthResponse;
import com.riwi.assesment.presentation.dto.LoginRequest;
import com.riwi.assesment.presentation.dto.RegisterRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for authentication endpoints.
 * Uses domain use cases for business logic.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints. These endpoints are public and don't require authentication.")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT token for immediate authentication. The token expires in 24 hours."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzUxMiJ9...",
                                              "username": "johndoe",
                                              "message": "User registered successfully"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - Username or email already exists, or validation failed",
                    content = @Content(
                            mediaType = "application/problem+json",
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Validation Error",
                                              "status": 400,
                                              "detail": "One or more fields failed validation",
                                              "instance": "/api/auth/register",
                                              "timestamp": "2025-01-15T10:30:00Z",
                                              "traceId": "550e8400-e29b-41d4-a716-446655440000",
                                              "errors": {
                                                "email": "must be a valid email"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(
                            name = "Registration Example",
                            value = """
                                    {
                                      "username": "johndoe",
                                      "email": "john.doe@example.com",
                                      "password": "SecurePass123!"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserUseCase.RegisterUserCommand command =
                new RegisterUserUseCase.RegisterUserCommand(
                        request.username(),
                        request.email(),
                        request.password()
                );

        RegisterUserUseCase.RegisterUserResult result = registerUserUseCase.execute(command);

        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, result.message()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(result.token(), result.user().getUsername(), result.message()));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login with credentials",
            description = "Authenticates a user with username and password, returning a JWT token valid for 24 hours."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzUxMiJ9...",
                                              "username": "johndoe",
                                              "message": "Login successful"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/problem+json",
                            examples = @ExampleObject(
                                    name = "Invalid Credentials",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Authentication Failed",
                                              "status": 401,
                                              "detail": "Invalid username or password",
                                              "instance": "/api/auth/login",
                                              "timestamp": "2025-01-15T10:30:00Z",
                                              "traceId": "550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "Login Example",
                            value = """
                                    {
                                      "username": "johndoe",
                                      "password": "SecurePass123!"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUserUseCase.LoginUserCommand command =
                new LoginUserUseCase.LoginUserCommand(
                        request.username(),
                        request.password()
                );

        LoginUserUseCase.LoginUserResult result = loginUserUseCase.execute(command);

        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, result.message()));
        }

        return ResponseEntity.ok(new AuthResponse(result.token(), result.user().getUsername(), result.message()));
    }
}
