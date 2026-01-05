package com.riwi.assesment.presentation.controller;

import com.riwi.assesment.domain.model.User;
import com.riwi.assesment.presentation.dto.AuthResponse;
import com.riwi.assesment.presentation.dto.LoginRequest;
import com.riwi.assesment.presentation.dto.RegisterRequest;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.riwi.assesment.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.riwi.assesment.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import com.riwi.assesment.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JpaUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, "Username already exists"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, "Email already exists"));
        }

        // Create user entity
        UserEntity userEntity = new UserEntity(
                UUID.randomUUID(),
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        // Save user
        UserEntity savedUser = userRepository.save(userEntity);

        // Generate token
        String token = jwtTokenProvider.generateToken(savedUser.getId(), savedUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, savedUser.getUsername(), "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Find user by username
        UserEntity user = userRepository.findByUsername(request.username())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid username or password"));
        }

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid username or password"));
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), "Login successful"));
    }
}
