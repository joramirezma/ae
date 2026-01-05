package com.riwi.assesment.application.service;

import com.riwi.assesment.domain.model.User;
import com.riwi.assesment.domain.port.in.LoginUserUseCase;
import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.domain.port.out.PasswordEncoderPort;
import com.riwi.assesment.domain.port.out.TokenProviderPort;
import com.riwi.assesment.domain.port.out.UserRepositoryPort;

import java.util.Optional;

/**
 * Service that implements the LoginUserUseCase.
 * Contains the business logic for user authentication.
 */
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final AuditLogPort auditLogPort;

    public LoginUserService(
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
    public LoginUserResult execute(LoginUserCommand command) {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(command.username());

        if (userOptional.isEmpty()) {
            return new LoginUserResult(null, null, false, "Invalid username or password");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            return new LoginUserResult(null, null, false, "Invalid username or password");
        }

        // Generate token
        String token = tokenProvider.generateToken(user.getId(), user.getUsername());

        // Register audit log
        auditLogPort.register("USER_LOGIN", user.getId());

        return new LoginUserResult(user, token, true, "Login successful");
    }
}
