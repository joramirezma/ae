package com.riwi.assesment.domain.port.in;

import com.riwi.assesment.domain.model.User;

/**
 * Input port for user registration.
 * This interface defines the use case contract for registering new users.
 */
public interface RegisterUserUseCase {

    /**
     * Command object containing the data needed to register a user.
     */
    record RegisterUserCommand(String username, String email, String password) {
        public RegisterUserCommand {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username cannot be null or blank");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank");
            }
        }
    }

    /**
     * Result object containing the registration result.
     */
    record RegisterUserResult(User user, String token, boolean success, String message) {}

    /**
     * Registers a new user in the system.
     * @param command the command containing user registration data
     * @return the registration result with user and token if successful
     */
    RegisterUserResult execute(RegisterUserCommand command);
}
