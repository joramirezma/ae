package com.riwi.assesment.domain.port.in;

import com.riwi.assesment.domain.model.User;

/**
 * Input port for user login.
 * This interface defines the use case contract for authenticating users.
 */
public interface LoginUserUseCase {

    /**
     * Command object containing the data needed to login.
     */
    record LoginUserCommand(String username, String password) {
        public LoginUserCommand {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username cannot be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank");
            }
        }
    }

    /**
     * Result object containing the login result.
     */
    record LoginUserResult(User user, String token, boolean success, String message) {}

    /**
     * Authenticates a user with the given credentials.
     * @param command the command containing login credentials
     * @return the login result with user and token if successful
     */
    LoginUserResult execute(LoginUserCommand command);
}
