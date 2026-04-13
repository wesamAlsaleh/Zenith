package com.avocadogroup.zenith.emailVerification;

import com.avocadogroup.zenith.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;

    @ColumnDefault("false")
    @Column(name = "used", nullable = false)
    private Boolean used = false;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    /**
     * Checks if the token is still valid.
     * * @return true if the token has not yet expired, false otherwise.
     */
    public boolean isValid() {
        // Return true if the expiration date is AFTER the current time
        return this.expiredAt != null && this.expiredAt.isAfter(Instant.now());
    }
}