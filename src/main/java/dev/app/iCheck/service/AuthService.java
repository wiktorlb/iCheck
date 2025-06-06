package dev.app.iCheck.service;

import dev.app.iCheck.model.User;
import dev.app.iCheck.repository.UserRepository;
import dev.app.iCheck.security.JWTUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service class for handling user authentication operations.
 * Provides methods for user login and token generation.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return JWT token if authentication is successful
     * @throws UsernameNotFoundException if the user is not found
     * @throws RuntimeException if the credentials are invalid
     */
    public String loginUser(String username, String password) {
        System.out.println("Attempting to login user: " + username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = userOptional.get();
        System.out.println("User found: " + user.getUsername());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        System.out.println("Password matches. Generating token...");
        return jwtUtil.generateToken(user.getUsername(), user.getRoles().get(0));
    }
}