package com.avocadogroup.zenith.passwordReset;

import com.avocadogroup.zenith.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    /**
     * Checks if the token is still valid.
     * @return true if the token has not yet expired, false otherwise.
     */
    public boolean isValid() {
        // Return true if the expiration date is after the current time
        return this.expiredAt != null && this.expiredAt.isAfter(Instant.now());
    }
}
