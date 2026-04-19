package com.avocadogroup.zenith.users;

import com.avocadogroup.zenith.common.exceptions.BadRequestException;
import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // function to make the user verified
    public void VerifyUser(User user) {
        // If the user is null throw error
        if (user == null) {
            throw new BadRequestException("User is null");
        }

        // Make the user verified
        user.verified();

        // Save the changes
        userRepository.save(user);
    }

    // Function to delete a user (soft delete for ADMIN, hard delete for others)
    public void deleteUser(Long userId) {
        // Fetch the user from the db
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If the user is an admin
        if (UserRole.ADMIN.name().equals(user.getRole())) {
            // If the user is already disable do nothing
            if (!user.getIsEnabled()) {
                return;
            }

            // Disable the admin user (soft delete)
            user.setIsEnabled(false);

            // Save the changes
            userRepository.save(user);
        } else {
            // remove non-admin user (Hard delete)
            userRepository.delete(user);
        }
    }
}
