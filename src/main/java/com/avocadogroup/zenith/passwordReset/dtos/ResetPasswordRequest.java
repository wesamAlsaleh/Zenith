package com.avocadogroup.zenith.passwordReset.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token must not be blank")
    private String token;

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String newPassword;
}
