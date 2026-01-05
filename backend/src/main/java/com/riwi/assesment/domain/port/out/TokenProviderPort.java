package com.riwi.assesment.domain.port.out;

import java.util.UUID;

/**
 * Output port for JWT token operations.
 * Abstracts the token generation mechanism from the domain.
 */
public interface TokenProviderPort {

    /**
     * Generates a JWT token for the given user.
     * @param userId the user's unique identifier
     * @param username the user's username
     * @return the generated JWT token
     */
    String generateToken(UUID userId, String username);

    /**
     * Validates a JWT token.
     * @param token the token to validate
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts the user ID from a token.
     * @param token the JWT token
     * @return the user ID
     */
    UUID getUserIdFromToken(String token);

    /**
     * Extracts the username from a token.
     * @param token the JWT token
     * @return the username
     */
    String getUsernameFromToken(String token);
}
