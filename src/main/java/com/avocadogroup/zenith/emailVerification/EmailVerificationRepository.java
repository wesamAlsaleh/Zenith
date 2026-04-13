package com.avocadogroup.zenith.emailVerification;

import com.avocadogroup.zenith.userSessions.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
}
