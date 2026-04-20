package com.avocadogroup.zenith.users;

import com.avocadogroup.zenith.users.dtos.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    // Function to update the authenticated user's profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @ModelAttribute UpdateProfileRequest request) {
        // Update the user profile
        var userDto = userService.updateProfile(request);

        // Return updated user with HTTP 200
        return ResponseEntity.ok(userDto);
    }

    // Function to delete user (soft delete for ADMIN, hard delete for others)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        // Try to delete the user
        userService.deleteUser(userId);

        // Return HTTP 200 response
        return ResponseEntity.noContent().build();
    }
}
