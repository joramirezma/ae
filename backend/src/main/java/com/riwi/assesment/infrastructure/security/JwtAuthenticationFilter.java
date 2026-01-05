package com.riwi.assesment.infrastructure.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter.
 * Intercepts requests and validates JWT tokens with detailed error handling.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                if (jwtTokenProvider.validateToken(jwt)) {
                    UUID userId = jwtTokenProvider.getUserIdFromToken(jwt);
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId.toString(),
                                    null,
                                    Collections.emptyList()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT token has expired: " + ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Token expired", "Your session has expired. Please login again.");
            return;
        } catch (MalformedJwtException ex) {
            logger.warn("Invalid JWT token format: " + ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Invalid token", "The provided token is malformed.");
            return;
        } catch (SignatureException ex) {
            logger.warn("Invalid JWT signature: " + ex.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Invalid signature", "Token signature validation failed.");
            return;
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, 
                                   String error, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        response.getWriter().write(String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status, error, message
        ));
    }
}
