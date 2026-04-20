package com.avocadogroup.zenith.users;

import com.avocadogroup.zenith.cloudinary.CloudinaryService;
import com.avocadogroup.zenith.common.exceptions.BadRequestException;
import com.avocadogroup.zenith.common.exceptions.DuplicateResourceException;
import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.zenith.users.dtos.UpdateProfileRequest;
import com.avocadogroup.zenith.users.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    /**
     * Verifies the user by setting the verified flag to true
     * @param user the user to verify
     */
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

    /**
     * Updates the authenticated user's profile (partial update)
     * @param request the update profile request
     * @return the updated user as DTO
     */
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {

        // Fetch the user from the db
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update email if provided
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Check if the new email is already taken by another user
            if (!request.getEmail().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already in use");
            }

            // Update the email
            user.setEmail(request.getEmail());
        }

        // Update phone number if provided
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            // Check if the new phone number is already taken by another user
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DuplicateResourceException("Phone number already in use");
            }
            
            // Update the phone number
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // Upload avatar if provided
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = cloudinaryService.uploadFile(request.getAvatar(), "avatars");

            // Update the avatar URL
            user.setAvatarUrl(avatarUrl);
        }

        // Save the changes
        userRepository.save(user);

        // Return updated user as DTO
        return userMapper.toDto(user);
    }

    /**
     * Deletes a user (soft delete for ADMIN, hard delete for others)
     * @param userId the ID of the user to delete
     */
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
