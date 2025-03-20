package dev.app.iCheck.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.app.iCheck.model.Aircraft;

public interface AircraftRepository extends MongoRepository<Aircraft, String> {
    Optional<Aircraft> findById(String id);
}