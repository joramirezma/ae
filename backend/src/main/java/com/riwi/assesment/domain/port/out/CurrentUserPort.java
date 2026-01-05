package com.riwi.assesment.domain.port.out;

import java.util.UUID;

/**
 * Output port for retrieving the current authenticated user.
 * This interface abstracts the security context from the domain.
 */
public interface CurrentUserPort {

    /**
     * Gets the ID of the currently authenticated user.
     * @return the UUID of the current user
     * @throws IllegalStateException if no user is authenticated
     */
    UUID getCurrentUserId();
}
