package dev.app.iCheck.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.repository.FlightRepository;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Passenger> getPassengersByFlightId(String flightId) {
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            return flight.get().getPassengers(); // Zakładamy, że lot ma listę pasażerów
        } else {
            throw new RuntimeException("Flight not found");
        }
    }
}
