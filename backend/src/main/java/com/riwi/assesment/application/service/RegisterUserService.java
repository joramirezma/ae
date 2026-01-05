package com.riwi.assesment.application.service;

import java.util.UUID;

import com.riwi.assesment.domain.model.User;
import com.riwi.assesment.domain.port.in.RegisterUserUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.PasswordEncoderPort;
import com.riwi.assesment.domain.port.out.TokenProviderPort;
import com.riwi.assesment.domain.port.out.UserRepositoryPort;

/**
 * Service that implements the RegisterUserUseCase.
 * Contains the business logic for user registration.
 */
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final AuditLogPort auditLogPort;

    public RegisterUserService(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider,
            AuditLogPort auditLogPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.auditLogPort = auditLogPort;
    }

    @Override
    public RegisterUserResult execute(RegisterUserCommand command) {
        // Check if username already exists
        if (userRepository.existsByUsername(command.username())) {
            return new RegisterUserResult(null, null, false, "Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(command.email())) {
            return new RegisterUserResult(null, null, false, "Email already exists");
        }

        // Create new user with encoded password
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(command.username())
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .build();

        // Persist the user
        User savedUser = userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getUsername());

        // Register audit log
        auditLogPort.register("USER_REGISTERED", savedUser.getId());

        return new RegisterUserResult(savedUser, token, true, "User registered successfully");
    }
}
