package com.riwi.assesment.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.riwi.assesment.domain.model.User;

/**
 * Output port for User persistence operations.
 * This interface defines the contract that any persistence adapter must implement.
 */
public interface UserRepositoryPort {

    /**
     * Saves a user to the database.
     * @param user the user to save
     * @return the saved user with generated ID if new
     */
    User save(User user);

    /**
     * Finds a user by their ID.
     * @param id the user ID
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(UUID id);

    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     * @param email the email to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given username exists.
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email exists.
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
