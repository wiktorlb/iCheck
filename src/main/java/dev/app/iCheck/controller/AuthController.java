package dev.app.iCheck.controller;

import dev.app.iCheck.model.LoginRequest;
import dev.app.iCheck.model.LoginResponse;
import dev.app.iCheck.model.User;
import dev.app.iCheck.repository.UserRepository;
import dev.app.iCheck.service.AuthService;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling user authentication, including login and registration.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    /**
     * Endpoint for user login.
     *
     * @param loginRequest The request body containing username and password.
     * @return ResponseEntity with a JWT token upon successful login or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Get token from the service
            String token = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

            // If the token is valid, return it in the response
            if (token != null) {
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                // If token is null, it means login failed
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            // Return appropriate status in case of errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    /**
     * Endpoint for user registration.
     *
     * @param user The User object containing registration details.
     * @return ResponseEntity indicating successful registration or an error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Validate unique email
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists: " + user.getEmail());
            }

            // Generate unique 6-digit username
            String username = generateUniqueUsername();
            user.setUsername(username);

            // Set default values
            user.setRoles(Collections.singletonList("USER"));
            user.setCreatedAt(Instant.now());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save user to database
            userRepository.save(user);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while registering user: " + e.getMessage());
        }
    }

/**
 * Generates a unique 6-digit username.
 *
 * @return A unique 6-digit username string.
 */
private String generateUniqueUsername() {
    Random random = new Random();
    String username;
    do {
        username = String.format("%06d", random.nextInt(1000000)); // 6-digit username
    } while (userRepository.existsByUsername(username)); // Check if username is unique
    return username;
}
}
