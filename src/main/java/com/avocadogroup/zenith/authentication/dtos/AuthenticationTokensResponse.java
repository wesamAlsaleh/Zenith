package com.avocadogroup.zenith.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationTokensResponse {
    private String token;
}
