package dev.app.iCheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.app.iCheck.model.User;
import dev.app.iCheck.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Pobieranie wszystkich użytkowników
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Dodawanie roli użytkownikowi
    @PutMapping("/{userId}/roles/add")
    public ResponseEntity<String> addRole(@PathVariable String userId, @RequestBody String role) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Upewnij się, że rola jest zapisana jako zwykły string
            role = role.replaceAll("\"", "").trim();

            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
                userRepository.save(user);
                return ResponseEntity.ok("Role added successfully.");
            }
            return ResponseEntity.status(400).body("Role already exists.");
        }
        return ResponseEntity.status(404).body("User not found.");
    }

   // Usuwanie roli użytkownika
   @PutMapping("/{userId}/roles/remove")
    public ResponseEntity<String> removeRole(@PathVariable String userId, @RequestBody String role) {
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isPresent()) {
        User user = userOptional.get();

        // Upewnij się, że rola jest zapisana jako zwykły string
        role = role.replaceAll("\"", "").trim();

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
            return ResponseEntity.ok("Role removed successfully.");
        }
        return ResponseEntity.status(400).body("Role not found.");
    }
    return ResponseEntity.status(404).body("User not found.");
}
}