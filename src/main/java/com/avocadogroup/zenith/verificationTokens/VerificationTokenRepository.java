package com.avocadogroup.zenith.verificationTokens;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    /**
     *
     * @param token
     * @return
     */
    VerificationToken findByToken(String token);
}
