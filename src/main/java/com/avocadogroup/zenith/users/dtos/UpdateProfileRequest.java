package com.avocadogroup.zenith.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileRequest {
    @Email(message = "Email must be a valid email address")
    @Size(max = 255)
    private String email;

    @Size(min = 8, max = 8, message = "Phone number must be exactly 8 digits")
    private String phoneNumber;

    private MultipartFile avatar;
}
