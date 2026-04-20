package com.avocadogroup.zenith.users.dtos;

import com.avocadogroup.zenith.users.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private Boolean isEnabled;
    private UserRole role;
    private String avatarUrl;
    private String phoneNumber;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean verified;
}
