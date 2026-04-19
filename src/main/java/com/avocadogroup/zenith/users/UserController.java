package com.avocadogroup.zenith.users;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    // Function to delete user (soft delete for ADMIN, hard delete for others)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        // Try to delete the user
        userService.deleteUser(userId);

        // Return HTTP 200 response
        return ResponseEntity.noContent().build();
    }
}
