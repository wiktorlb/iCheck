package dev.app.iCheck.repository;

import dev.app.iCheck.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // Definiujemy metodę do wyszukiwania użytkownika po nazwie użytkownika
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

     Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}