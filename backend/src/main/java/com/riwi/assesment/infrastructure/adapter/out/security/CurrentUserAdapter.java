package com.riwi.assesment.infrastructure.adapter.out.security;

import com.riwi.assesment.domain.port.out.CurrentUserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter implementing CurrentUserPort.
 * Retrieves the current authenticated user from Spring Security context.
 */
@Component
public class CurrentUserAdapter implements CurrentUserPort {

    @Override
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UUID) {
            return (UUID) principal;
        }
        
        if (principal instanceof String) {
            try {
                return UUID.fromString((String) principal);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid user ID format in security context");
            }
        }

        throw new IllegalStateException("Unable to extract user ID from security context");
    }
}
