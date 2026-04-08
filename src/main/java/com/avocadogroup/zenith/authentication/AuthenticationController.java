package com.avocadogroup.zenith.authentication;

import com.avocadogroup.zenith.authentication.dtos.LoginUserRequest;
import com.avocadogroup.zenith.authentication.dtos.LoginUserResponse;
import com.avocadogroup.zenith.authentication.dtos.RegisterUserRequest;
import com.avocadogroup.zenith.authentication.services.AuthenticationService;
import com.avocadogroup.zenith.common.exceptions.BadRequestException;
import com.avocadogroup.zenith.users.dtos.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;

    /**
     * Health check or test endpoint.
     *
     * <p>This endpoint is used to verify that the service is running and reachable.
     * It returns a simple static response.</p>
     *
     * @return a {@link ResponseEntity} containing a greeting message ("Hello World")
     */
    @GetMapping("/ping")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    /**
     * A secured heartbeat endpoint used to verify authentication and service availability.
     * <p>
     * This endpoint requires a valid security context (e.g., JWT or Session) to access.
     * It returns a simple success message to confirm that the requester is properly
     * authorized and the server is responsive.
     * </p>
     *
     * @return A {@link ResponseEntity} containing a "Protected Hello World" success message.
     */
    @GetMapping("/protected-ping")
    public ResponseEntity<String> protectedPing() {
        return ResponseEntity.ok("Protected Hello World");
    }

    /**
     * Registers a new user in the system.
     *
     * <p>This endpoint receives user registration data, delegates the creation
     * process to the authentication service, and returns the created user along
     * with a location header pointing to the newly created resource.</p>
     *
     * @param registerUserRequest the validated request body containing user registration data
     * @param uriBuilder          utility used to construct the URI of the newly created resource
     * @return a {@link ResponseEntity} containing the created {@link UserDto} and HTTP 201 status
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest registerUserRequest,
            UriComponentsBuilder uriBuilder
    ) {
        // Delegate user creation to the authentication service
        var userDto = authenticationService.register(registerUserRequest); // through exception if something went wrong

        // Build URI for the newly created user resource (REST best practice)
        var uri = uriBuilder
                .path("/users/{id}")
                .buildAndExpand(userDto.getId())
                .toUri();

        // Return HTTP 201 Created with Location header and created user payload
        return ResponseEntity.created(uri).body(userDto);
    }

    /**
     * Authenticates a user and returns a JWT access token.
     *
     * <p>This endpoint validates the provided credentials and delegates authentication
     * to the authentication service. If authentication succeeds, a JWT access token
     * is generated and returned in the response body.</p>
     *
     * <p>This implementation is stateless and does not use refresh tokens or cookies.</p>
     *
     * @param request the validated login request containing email and password
     * @return a {@link ResponseEntity} containing the JWT access token
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginUserRequest request
    ) {
        // Authenticate user and generate token (throws exception if credentials are invalid or user not found)
        var authenticationTokens = authenticationService.login(request);

        // Return HTTP 200 OK with access token in response body
        return ResponseEntity.ok().body(new LoginUserResponse(authenticationTokens.getToken()));
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     * <p>
     * This endpoint extracts the user context from the security session or token
     * and returns a DTO containing the user's account details.
     * </p>
     *
     * @return A {@link ResponseEntity} containing the {@code UserDto} of the requester.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        // Retrieves the current user profile from the security context
        var userDto = authenticationService.me();

        // Return the user details with OK response
        return ResponseEntity.ok(userDto);
    }

    /**
     * Processes user logout by extracting and invalidating the provided JWT.
     * <p>
     * This endpoint directly captures the <b>Authorization</b> header. If the header
     * is valid and follows the Bearer scheme, the token is sent to the
     * {@code AuthenticationService} for revocation. A 204 No Content status
     * confirms the operation succeeded.
     * </p>
     *
     * @param authorizationHeader The raw "Bearer [token]" string from the request headers.
     * @return A {@link ResponseEntity} with HTTP Status 204 (No Content).
     * @throws BadRequestException If the header is missing, empty, or not a Bearer token.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // If the authorization header is missing or does not start with "Bearer " then skip the filter
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // System.out.println("Authorization header is null");
            throw new BadRequestException("Invalid logout request");
        }

        // Isolates the JWT string by removing the "Bearer " prefix
        var token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from "Bearer A2C4"

        // Triggers the backend service to mark the token as revoked
        var revokedToken = authenticationService.logout(token);

        // Returns 204 No Content as the standard successful response for destructive actions
        return ResponseEntity.noContent().build();
    }
}
