package com.avocadogroup.zenith.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Function to get the user by email

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address used to search for the user
     * @return an {@link Optional} containing the {@link User} if found, otherwise an empty Optional
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Checks whether a user exists with the given email.
     *
     * @param email the email address to check
     * @return {@code true} if a user with the given email exists, otherwise {@code false}
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a user exists with the given phone number.
     *
     * @param phoneNumber the phone number to check
     * @return {@code true} if a user with the given phone number exists, otherwise {@code false}
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
