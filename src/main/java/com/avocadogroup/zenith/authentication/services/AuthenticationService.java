package com.avocadogroup.zenith.authentication.services;

import com.avocadogroup.zenith.authentication.UserDetailsImpl;
import com.avocadogroup.zenith.authentication.dtos.AuthenticationTokensResponse;
import com.avocadogroup.zenith.authentication.dtos.LoginUserRequest;
import com.avocadogroup.zenith.authentication.dtos.RegisterUserRequest;
import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.zenith.users.User;
import com.avocadogroup.zenith.users.UserMapper;
import com.avocadogroup.zenith.users.UserRepository;
import com.avocadogroup.zenith.users.UserRole;
import com.avocadogroup.zenith.users.dtos.UserDto;
import com.avocadogroup.zenith.verificationTokens.VerificationToken;
import com.avocadogroup.zenith.verificationTokens.VerificationTokenRepository;
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
    private final VerificationTokenRepository verificationTokenRepository;

    /**
     * Extracts the authenticated user's ID from the Security Context.
     *
     * <p>This method retrieves the {@link Authentication} object from the
     * {@link SecurityContextHolder}, which stores security information in a
     * thread-local context for the current request.</p>
     *
     * <p>It assumes that the {@code principal} has been set (typically during
     * JWT authentication filtering) and contains the user's unique identifier.</p>
     *
     * @return the unique {@link Long} identifier of the currently authenticated user
     * @throws RuntimeException if the authentication object is {@code null} or
     *                          if the principal is missing, indicating an unauthorized
     *                          or improperly processed request
     * @throws NumberFormatException if the principal cannot be parsed into a {@link Long}
     */
    private Long getUserIdFromSecurityContext() {
        // Retrieve the current authentication details from the security thread-local storage
        var authenticationObject = SecurityContextHolder.getContext().getAuthentication(); // Authentication is the object that holds the authentication information of the user

        // Ensure the authentication object exists before attempting to access the principal
        if (authenticationObject == null) {
            // TODO: Replace with a custom exception (e.g., UnauthorizedException)
            throw new RuntimeException("Authentication object is null");
        }

        // Extract and convert the principal (expected to be the user ID) into a Long
        // Objects.requireNonNull ensures we don't call .toString() on a null principal
        return Long.parseLong(Objects.requireNonNull(authenticationObject.getPrincipal()).toString());
    }

    /**
     * Registers a new user in the system.
     *
     * <p>This method creates a new {@link User} entity from the provided request,
     * encodes the user's password, assigns a default role, persists the user in
     * the database, and returns a mapped {@link UserDto} representation.</p>
     *
     * <p>Note: Additional validation (such as checking for existing email or phone
     * number) and email verification are expected to be handled in future enhancements.</p>
     *
     * @param request the registration request containing user input data
     * @return a {@link UserDto} representing the newly created user
     */
    public UserDto register(RegisterUserRequest request) {
        // Initialize a new User entity
        var user = new User();

        // TODO: validate that email and phone number do not already exist

        // Set the user fields from request
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER.toString()); // Assign default role for newly registered users

        // Save the user entity to the database
        userRepository.save(user);

        // TODO: trigger email verification workflow after registration

        // Return the created entity as a DTO
        return userMapper.toDto(user);
    }

    /**
     * Authenticates a user and generates an access token.
     *
     * <p>This method delegates authentication to the {@link AuthenticationManager}.
     * If authentication succeeds, the authenticated principal is extracted,
     * and a JWT access token is generated for the user.</p>
     *
     * <p>The generated token is persisted in a {@link VerificationToken} entity
     * along with its expiration date for tracking or revocation purposes.</p>
     *
     * @param request the login request containing user credentials (email and password)
     * @return an {@link AuthenticationTokensResponse} containing the generated access token
     */
    public AuthenticationTokensResponse login(LoginUserRequest request) {
        // Authenticate user credentials using Spring Security AuthenticationManager by providing the email and password to the manager, if failed it will throw an exception
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get the UserDetails object from the security context after login
        var userObj = (UserDetailsImpl) authentication.getPrincipal(); // Authenticated principal (custom UserDetails implementation)

        // Get the User entity from the authenticated principal (from the UserDetails object)
        assert userObj != null;
        var user = userObj.getUser();

        // Generate an access token (JWT) for the authenticated user
        var accessToken = jwtService.generateAccessToken(user);

        // Create new verification token record
        var verificationToken = new VerificationToken();

        // Set the token metadata
        verificationToken.setUser(user);
        verificationToken.setToken(accessToken);
        verificationToken.setExpiresAt(jwtService.getTokenExpiryDate(accessToken));

        // Save the token in database for session tracking
        verificationTokenRepository.save(verificationToken);

        // Return authentication response containing the access token {accessToken:"abc", refreshToken:"xyz"}
        return new AuthenticationTokensResponse(accessToken);
    }

    /**
     * Retrieves the currently authenticated user's profile.
     *
     * <p>This method extracts the user ID from the Security Context and uses it
     * to fetch the corresponding {@link User} entity from the database.</p>
     *
     * @return a {@link UserDto} representing the currently authenticated user
     * @throws ResourceNotFoundException if no user exists for the authenticated user ID
     */
    public UserDto me() {
        // Retrieve the authenticated user ID from the Security Context (user id is the principal in the security context holder)
        var userId = getUserIdFromSecurityContext();

        // Fetch user from database using the authenticated user ID
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Return the user as UserDto format
        return userMapper.toDto(user);
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * <p>This method delegates the extraction of the user ID to the security context,
     * which typically holds authentication details for the current request.</p>
     *
     * @return the unique identifier of the authenticated user, or {@code null}
     *         if no authentication information is available
     */
    public Long getUserId() {
        // Extracts the user ID from the SecurityContext
        return getUserIdFromSecurityContext();
    }
}
