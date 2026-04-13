package com.avocadogroup.zenith.users;

import com.avocadogroup.zenith.common.exceptions.BadRequestException;
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
}
