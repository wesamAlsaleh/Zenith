package com.avocadogroup.zenith.common.configs;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtConfig {
    // This properties will be loaded from application.yaml file
    private String secretKey;
    private Long accessTokenValiditySeconds;
    private Long refreshTokenValiditySeconds;

    /**
     * Converts the raw string secret key into a {@link SecretKey} instance.
     * <p>
     * This method uses the HMAC-SHA algorithm to generate a secure signing key
     * from the application's configured secret string. This key is used
     * for signing and verifying JWT (JSON Web Token) signatures.
     * </p>
     *
     * @return a {@link SecretKey} object suitable for HMAC-SHA cryptographic operations.
     * @throws IllegalArgumentException if the {@code secretKey} is null or does not meet
     * the minimum length requirements for the algorithm.
     */
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
