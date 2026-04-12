package com.avocadogroup.zenith.emailVerification;

import com.avocadogroup.zenith.email.EmailService;
import com.avocadogroup.zenith.email.dtos.SimpleEmailRequest;
import com.avocadogroup.zenith.emailVerification.dtos.SendEmailVerificationTokenRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@AllArgsConstructor
public class EmailVerificationService {
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * Generates a cryptographically secure, URL-safe string to be used
     * for email verification.
     * * @return A unique 32-byte Base64 encoded token.
     */
    private String generateEmailVerificationToken() {
        // Initialize array of 32 byte
        byte[] bytes = new byte[32];

        // Fill the byte array with cryptographically strong random values
        new SecureRandom().nextBytes(bytes);

        // Return a URL-safe string without padding (e.g., '+' and '/' replaced, '=' removed)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Function to send verification email
    public void sendVerificationEmail(SendEmailVerificationTokenRequest request) {
        // Generate verification token
        var verificationToken = generateEmailVerificationToken();

        // Prepare the body
        var body = "Your verification token is: " + verificationToken + "\n\nThis token expires in 15 minutes.";

        // Prepare the expiration time
        var expiresAt = Instant.now().plusSeconds(900);

        // Create the token entity
        var token = new EmailVerificationToken();
        token.setUser(request.getUser());
        token.setToken(verificationToken);
        token.setExpiredAt(expiresAt);

        // Email the user
        emailService.sendEmail(new SimpleEmailRequest(request.getUser().getEmail(), "Email Verification", body));

        // Save the token in the db
        emailVerificationRepository.save(token);
    }
}
