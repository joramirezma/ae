package com.riwi.assesment.domain.port.out;

/**
 * Output port for password encoding operations.
 * Abstracts the password encoding mechanism from the domain.
 */
public interface PasswordEncoderPort {

    /**
     * Encodes a raw password.
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */
    String encode(String rawPassword);

    /**
     * Verifies if a raw password matches an encoded password.
     * @param rawPassword the raw password to check
     * @param encodedPassword the encoded password to compare against
     * @return true if passwords match, false otherwise
     */
    boolean matches(String rawPassword, String encodedPassword);
}
