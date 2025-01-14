package dev.app.iCheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.app.iCheck.model.Flight;

public interface FlightRepository extends MongoRepository<Flight, String> {
}

// package dev.app.iCheck.repository;

// import org.springframework.data.mongodb.repository.MongoRepository;
// import dev.app.iCheck.model.Flight;

// public interface FlightRepository extends MongoRepository<Flight, String> {
// }