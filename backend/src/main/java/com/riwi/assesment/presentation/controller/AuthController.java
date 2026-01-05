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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for authentication endpoints.
 * Uses domain use cases for business logic.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
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
    @Operation(summary = "Login with username and password")
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
