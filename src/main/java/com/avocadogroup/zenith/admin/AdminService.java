package com.avocadogroup.zenith.admin;

import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.zenith.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminService {
    private final UserRepository userRepository;


}
