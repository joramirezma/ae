package com.riwi.assesment.infrastructure.adapter.out.security;

import com.riwi.assesment.domain.port.out.TokenProviderPort;
import com.riwi.assesment.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Security adapter implementing TokenProviderPort.
 * Adapts the domain port to JWT token provider.
 */
@Component
public class TokenProviderAdapter implements TokenProviderPort {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenProviderAdapter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String generateToken(UUID userId, String username) {
        return jwtTokenProvider.generateToken(userId, username);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public UUID getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}
