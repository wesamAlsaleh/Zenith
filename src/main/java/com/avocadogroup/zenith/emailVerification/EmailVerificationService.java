package com.avocadogroup.zenith.emailVerification;

import com.avocadogroup.zenith.email.EmailService;
import com.avocadogroup.zenith.email.dtos.SimpleEmailRequest;
import com.avocadogroup.zenith.emailVerification.dtos.SendEmailVerificationTokenRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@AllArgsConstructor
public class EmailVerificationService {
    private final EmailService emailService;

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
        var token = generateEmailVerificationToken();

        // Send an email to the user
        emailService.sendEmail(new SimpleEmailRequest(request.getToEmail(), "Test", "Test description"));

        // Save the token in the db


    }
}
