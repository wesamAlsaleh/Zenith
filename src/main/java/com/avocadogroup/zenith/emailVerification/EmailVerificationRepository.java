package com.avocadogroup.zenith.emailVerification;

import com.avocadogroup.zenith.userSessions.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, Long> {
}
