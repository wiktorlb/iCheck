package dev.app.iCheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dev.app.iCheck.model.User;
import dev.app.iCheck.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

     @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/roles/add")
    public ResponseEntity<String> addRole(@PathVariable String userId, @RequestBody String role) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

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

@DeleteMapping("/{userId}")
public ResponseEntity<String> deleteUser(@PathVariable String userId) {
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isPresent()) {
        userRepository.delete(userOptional.get());
        return ResponseEntity.ok("User deleted successfully.");
    } else {
        return ResponseEntity.status(404).body("User not found.");
    }
}
}