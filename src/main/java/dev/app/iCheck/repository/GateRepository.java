package dev.app.iCheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.app.iCheck.model.Gate;

@Repository
public interface GateRepository extends MongoRepository<Gate, String> {
}
