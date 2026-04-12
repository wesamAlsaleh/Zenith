package com.avocadogroup.zenith.emailVerification.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SendEmailVerificationTokenRequest {
    private String toEmail;
}
