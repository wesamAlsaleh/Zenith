package com.avocadogroup.zenith.emailVerification;

import com.avocadogroup.zenith.common.configs.AppConfig;
import com.avocadogroup.zenith.common.exceptions.BadRequestException;
import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.zenith.email.EmailService;
import com.avocadogroup.zenith.email.dtos.SimpleEmailRequest;
import com.avocadogroup.zenith.emailVerification.dtos.SendEmailVerificationTokenRequest;
import com.avocadogroup.zenith.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@AllArgsConstructor
public class EmailVerificationService {
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final AppConfig appConfig; // Injected url as a bean

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
        var verificationLink = appConfig.getBaseUrl() + "/api/auth/verify?token=" + verificationToken;

        var body = "Click the link below to verify your account:\n\n"
                + verificationLink
                + "\n\nThis link expires in 15 minutes.";

        // Prepare the expiration time
        var expiresAt = Instant.now().plusSeconds(900);

        // Create the token entity
        var token = new EmailVerificationToken();
        token.setUser(request.getUser());
        token.setToken(verificationToken);
        token.setExpiredAt(expiresAt);

        // Save the token in the db
        emailVerificationRepository.save(token);

        // Email the user
        emailService.sendEmail(new SimpleEmailRequest(request.getUser().getEmail(), "Email Verification", body));
    }

    // Function to verify a token
    public void verifyToken(String verificationToken) {
        // Fetch the token from the db
        var token = emailVerificationRepository.findByToken(verificationToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        // Check if the token is used
        if (token.getUsed()) {
            throw new BadRequestException("Token is already used");
        }

        // Check if the token is not valid (expired)
        if (!token.isValid()) {
            throw new BadRequestException("Token is invalid");
        }

        // Make the token as used
        token.setUsed(true);

        // Verify the user
        var user = token.getUser();
        user.setVerified(true);

        // Save the changes of the user
        userRepository.save(user);

        // Update the changes of the token
        emailVerificationRepository.save(token);
    }
}
