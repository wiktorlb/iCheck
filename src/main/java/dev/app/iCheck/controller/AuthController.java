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

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    // Endpoint do logowania, zwraca token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Uzyskujemy token z serwisu
            String token = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

            // Jeżeli token jest poprawny, zwracamy go w odpowiedzi
            if (token != null) {
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                // Jeśli token jest null, oznacza to błąd logowania
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            // W przypadku błędów zwróć odpowiedni status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    // Endpoint do rejestracji użytkownika
/*     @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        // Przypisanie roli "user" podczas rejestracji
        user.setRoles(Collections.singletonList("user")); // Domyślna rola
        user.setCreatedAt(Instant.now()); // Ustawienie daty utworzenia
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Haszowanie hasła

        // Zapisanie użytkownika do bazy
        userRepository.save(user);

        return "User registered successfully";
    } */

@PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody User user) {
    try {
        // Walidacja unikalności email
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists: " + user.getEmail());
        }

        // Losowanie unikalnego 6-cyfrowego username
        String username = generateUniqueUsername();
        user.setUsername(username);

        // Ustawienie domyślnych wartości
        user.setRoles(Collections.singletonList("USER"));
        user.setCreatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Zapis użytkownika w bazie
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error while registering user: " + e.getMessage());
    }
}

// Funkcja do losowania unikalnego 6-cyfrowego username
private String generateUniqueUsername() {
    Random random = new Random();
    String username;
    do {
        username = String.format("%06d", random.nextInt(1000000)); // 6-cyfrowe username
    } while (userRepository.existsByUsername(username)); // Sprawdzamy, czy username jest unikalny
    return username;
}
}
