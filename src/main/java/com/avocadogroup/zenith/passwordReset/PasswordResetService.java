package com.avocadogroup.zenith.passwordReset;

import com.avocadogroup.zenith.common.configs.AppConfig;
import com.avocadogroup.zenith.common.exceptions.BadRequestException;
import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.zenith.email.EmailService;
import com.avocadogroup.zenith.email.dtos.SimpleEmailRequest;
import com.avocadogroup.zenith.passwordReset.dtos.ResetPasswordRequest;
import com.avocadogroup.zenith.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@AllArgsConstructor
public class PasswordResetService {
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppConfig appConfig;

    /**
     * Generates a cryptographically secure, URL-safe string to be used
     * for password reset.
     * @return A unique 32-byte Base64 encoded token.
     */
    private String generatePasswordResetToken() {
        // Initialize array of 32 byte
        byte[] bytes = new byte[32];

        // Fill the byte array with cryptographically strong random values
        new SecureRandom().nextBytes(bytes);

        // Return a URL-safe string without padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Function to send password reset email
    public void sendPasswordResetEmail(String email) {
        // Fetch the user from the db
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate password reset token
        var resetToken = generatePasswordResetToken();

        // Prepare the body
        var resetLink = appConfig.getBaseUrl() + "/api/auth/reset-password?token=" + resetToken;

        var body = "Click the link below to reset your password:\n\n"
                + resetLink
                + "\n\nThis link expires in 15 minutes."
                + "\n\nIf you did not request a password reset, please ignore this email.";

        // Prepare the expiration time (15 minutes)
        var expiresAt = Instant.now().plusSeconds(900);

        // Create the token entity
        var token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(resetToken);
        token.setExpiredAt(expiresAt);

        // Save the token in the db
        passwordResetTokenRepository.save(token);

        // Email the user
        emailService.sendEmail(new SimpleEmailRequest(user.getEmail(), "Password Reset", body));
    }

    // Function to reset the password using the token
    public void resetPassword(ResetPasswordRequest request) {
        // Fetch the token from the db
        var token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        // Check if the token is used
        if (token.getUsed()) {
            throw new BadRequestException("Token is already used");
        }

        // Check if the token is not valid (expired)
        if (!token.isValid()) {
            throw new BadRequestException("Token is expired");
        }

        // Mark the token as used
        token.setUsed(true);

        // Update the user's password
        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Save the changes
        userRepository.save(user);
        passwordResetTokenRepository.save(token);
    }
}
