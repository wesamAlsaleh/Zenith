package com.avocadogroup.zenith.userSessions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionsRepository extends JpaRepository<UserSessions, Long> {
    /**
     * Retrieves a verification token by its token value.
     *
     * @param token the token value used to identify the verification record
     * @return an {@link Optional} containing the matching {@link UserSessions},
     * or {@link Optional#empty()} if no token is found
     */
    Optional<UserSessions> findByToken(String token);

    /**
     * Retrieves the most recent verification token associated with a specific user.
     * <p>
     * Returns an {@link Optional} to explicitly handle cases where a token
     * may not yet have been generated or has already been purged.
     * </p>
     *
     * @param userId The unique identifier of the user.
     * @return An {@link Optional} containing the {@link UserSessions} if found, otherwise empty.
     */
    Optional<UserSessions> findByUserId(Long userId);
}
