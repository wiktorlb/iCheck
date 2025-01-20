package dev.app.iCheck.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* import dev.app.iCheck.exception.GlobalExceptionHandler.ResourceNotFoundException; */
import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    public List<Passenger> getPassengersByFlightId(String flightId) {
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            return flight.get().getPassengers(); // Zakładamy, że lot ma listę pasażerów
        } else {
            throw new RuntimeException("Flight not found");
        }
    }

    public void deletePassenger(String flightId, String passengerId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        // Sprawdź, czy pasażer jest przypisany do tego lotu
        if (!flight.getPassengers().contains(passenger)) {
            throw new ResourceNotFoundException("Passenger not found in this flight");
        }

        // Usuń pasażera z listy pasażerów w locie
        flight.getPassengers().remove(passenger);

        /* // Zapisz zaktualizowany lot (jeśli to konieczne)
        flightRepository.save(flight); */

        // Usuń pasażera z bazy danych
        passengerRepository.delete(passenger);
        System.out.println("Passenger deleted from database");
    }
}
