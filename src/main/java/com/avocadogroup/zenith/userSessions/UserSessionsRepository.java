package com.avocadogroup.zenith.userSessions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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
     * Retrieves all active session tokens associated with a specific user.
     * <p>
     * Returns a list to support multiple concurrent sessions (e.g. multiple devices).
     * </p>
     *
     * @param userId The unique identifier of the user.
     * @return A list of {@link UserSessions} for the given user (may be empty).
     */
    List<UserSessions> findAllByUserId(Long userId);
}
