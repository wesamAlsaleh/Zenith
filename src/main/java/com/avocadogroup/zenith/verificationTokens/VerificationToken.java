package com.avocadogroup.zenith.verificationTokens;

import com.avocadogroup.zenith.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "revoked")
    private boolean revoked;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // Function to check if the token is valid

    /**
     *
     * @param userId
     * @return
     */
    public boolean isValid(Long userId) {
        // If the token does not belong to the real user
        if (user == null || user.getId() == null || !user.getId().equals(userId)) {
            return false;
        }

        // If the token is expired return false
        if (expiresAt.isBefore(Instant.now())) {
            return false;
        }

        // Return true if the token is not revoked
        return !revoked;
    }

    /**
     * Invalidates the current token by marking it as revoked and recording the exact timestamp.
     * <p>
     * This change is typically persisted to the database to prevent further
     * authentication attempts using this specific token.
     * </p>
     */
    public void revokeToken() {
        // Marks the token as invalid for future authentication checks
        this.revoked = true;

        // Captures the precise moment of revocation for audit and security tracking
        this.revokedAt = Instant.now();
    }
}