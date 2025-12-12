package dev.app.iCheck.repository;

import dev.app.iCheck.model.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    List<Passenger> findByFlightId(String flightId);

    long countByFlightId(String flightId);

    long countByFlightIdAndStatus(String flightId, String status);
}