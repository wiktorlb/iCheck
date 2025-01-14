package dev.app.iCheck.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.app.iCheck.model.Flight;

@Repository
public interface FlightRepository extends MongoRepository<Flight, String> {
    List<Flight> findByDepartureDate(String departureDate);
}

// package dev.app.iCheck.repository;

// import org.springframework.data.mongodb.repository.MongoRepository;
// import dev.app.iCheck.model.Flight;

// public interface FlightRepository extends MongoRepository<Flight, String> {
// }