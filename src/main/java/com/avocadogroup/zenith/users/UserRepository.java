package com.avocadogroup.zenith.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Function to get the user by email
    Optional<User> findByEmail(String email);
}
