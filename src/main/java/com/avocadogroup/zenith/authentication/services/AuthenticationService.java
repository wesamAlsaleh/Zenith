package com.avocadogroup.zenith.authentication.services;

import com.avocadogroup.zenith.authentication.UserDetailsImpl;
import com.avocadogroup.zenith.authentication.dtos.AuthenticationTokensResponse;
import com.avocadogroup.zenith.authentication.dtos.LoginUserRequest;
import com.avocadogroup.zenith.authentication.dtos.RegisterUserRequest;
import com.avocadogroup.zenith.users.User;
import com.avocadogroup.zenith.users.UserMapper;
import com.avocadogroup.zenith.users.UserRepository;
import com.avocadogroup.zenith.users.UserRole;
import com.avocadogroup.zenith.users.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Extracts the authenticated User ID from the global Security Context.
     * <p>
     * This helper method retrieves the {@link Authentication} object from the
     * {@link SecurityContextHolder}. It assumes the 'principal' has been populated
     * (typically during the JWT filtering stage) with the unique identifier of the user.
     * </p>
     *
     * @return the unique {@link Long} identifier of the currently authenticated user.
     * @throws RuntimeException if the security context is empty or the authentication
     *                          object is missing, indicating an unauthorized or improperly filtered request.
     */
    private Long getUserIdFromSecurityContext() {
        // Retrieve the current authentication details from the security thread-local storage
        var authenticationObject = SecurityContextHolder.getContext().getAuthentication(); // Authentication is the object that holds the authentication information of the user

        // Ensure the authentication object exists before attempting to access the principal
        if (authenticationObject == null) {
            // TODO: Replace with a specialized 'UnauthorizedException' or 'SecurityContextException'
            throw new RuntimeException("Authentication object is null");
        }

        // Extract the principal (User ID) and convert it to Long
        // Objects.requireNonNull ensures we don't call .toString() on a null principal
        return Long.parseLong(Objects.requireNonNull(authenticationObject.getPrincipal()).toString());
    }

    /**
     * function to register new user
     *
     * @param request
     * @return
     */
    public UserDto register(RegisterUserRequest request) {
        // Create new entity
        var user = new User();

        // Set the user data
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER.toString()); // default role

        // Save the record in the db
        userRepository.save(user);

        // Return the user as UserDto object
        return userMapper.toDto(user);
    }

    public AuthenticationTokensResponse login(LoginUserRequest request) {
        // Try to sign in the user by providing the email and password to the manager, if failed it will throw an exception
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get the UserDetails object from the security context after login
        var userObj = (UserDetailsImpl) authentication.getPrincipal();

        // Get the user from the UserDetails object
        assert userObj != null;
        var user = userObj.getUser();

        // Generate an access token (JWT) for the authenticated user
        var accessToken = jwtService.generateAccessToken(user);

        // TODO: save the access token in the database

        // Wrap and return the token in a AuthenticationTokensResponse object {accessToken:"abc", refreshToken:"xyz"}
        return new AuthenticationTokensResponse(accessToken);
    }
}
