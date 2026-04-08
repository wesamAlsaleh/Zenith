package com.avocadogroup.zenith.verificationTokens;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    /**
     * Retrieves a verification token by its token value.
     *
     * @param token the token value used to identify the verification record
     * @return an {@link Optional} containing the matching {@link VerificationToken},
     *         or {@link Optional#empty()} if no token is found
     */
    Optional<VerificationToken> findByToken(String token);
}
