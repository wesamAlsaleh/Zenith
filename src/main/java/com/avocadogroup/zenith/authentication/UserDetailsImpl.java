package com.avocadogroup.zenith.authentication;

import com.avocadogroup.zenith.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    @Getter
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the authorities (roles/permissions - empty for now)
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        // Return the hashed password
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Return the unique field (user email)
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        // Account must be both enabled and verified via email
        return Boolean.TRUE.equals(user.getIsEnabled()) && Boolean.TRUE.equals(user.getVerified());
    }
}
