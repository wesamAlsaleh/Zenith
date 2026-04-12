package com.avocadogroup.zenith.emailVerification.dtos;

import com.avocadogroup.zenith.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SendEmailVerificationTokenRequest {
    private User user;
}
