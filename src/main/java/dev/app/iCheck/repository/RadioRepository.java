package dev.app.iCheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.app.iCheck.model.Radio;

@Repository
public interface RadioRepository extends MongoRepository<Radio, String> {
}
