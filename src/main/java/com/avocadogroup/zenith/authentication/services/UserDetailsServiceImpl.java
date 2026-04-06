package com.avocadogroup.zenith.authentication.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Note that UserDetailsService is a core interface in Spring Security, and it expects exception to UsernameNotFoundException, not custom exceptions

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
