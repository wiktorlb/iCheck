package dev.app.iCheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Destination;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.repository.DestinationRepository;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.service.FlightService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private FlightService flightService;

    @Autowired
    private PassengerRepository passengerRepository;

    // Endpoint do dodawania lotów
    @PostMapping
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        try {
            // Walidacja trasy
            String[] routeParts = flight.getRoute().split(" - ");
            if (routeParts.length != 2) {
                return ResponseEntity.badRequest().body("Invalid route format. Expected 'KTW - DEST_ID'.");
            }

            String destinationCode = routeParts[1];
            Optional<Destination> destinationOpt = destinationRepository.findById(destinationCode);

            if (destinationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Destination code not found: " + destinationCode);
            }

            // Zapis lotu w bazie danych
            flight.setState("Prepare");
            Flight savedFlight = flightRepository.save(flight);

            return ResponseEntity.ok(savedFlight);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while saving flight: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getFlights(@RequestParam(required = false) String date) {
        try {
            List<Flight> flights = (date != null && !date.isEmpty())
                    ? flightRepository.findByDepartureDate(date)
                    : flightRepository.findAll();

            return ResponseEntity.ok(flights); // Zwracaj zawsze listę
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Wystąpił błąd: " + e.getMessage());
        }
    }

    // Endpoint do pobierania miejsc docelowych
    @GetMapping("/destinations")
    public ResponseEntity<?> getDestinations() {
        try {
            return ResponseEntity.ok(destinationRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while fetching destinations: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/passengers")
   public ResponseEntity<?> getPassengersByFlightId(@PathVariable("id") String flightId) {
       try {
           List<Passenger> passengers = passengerRepository.findByFlightId(flightId);
           return ResponseEntity.ok(passengers);
       } catch (Exception e) {
           return ResponseEntity.status(500).body("Wystąpił błąd: " + e.getMessage());
       }
   }

   @DeleteMapping("/{flightId}/passengers/{passengerId}")
   public ResponseEntity<?> deletePassenger(
           @PathVariable String flightId,
           @PathVariable String passengerId) {
       try {
           Flight flight = flightRepository.findById(flightId)
                   .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
           Passenger passenger = passengerRepository.findById(passengerId)
                   .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

           flight.getPassengers();
           flightRepository.save(flight);
           passengerRepository.delete(passenger);

           return ResponseEntity.ok("Passenger deleted successfully");
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
   }

 @DeleteMapping("/{flightId}")
public ResponseEntity<?> deleteFlight(@PathVariable String flightId) {
    try {
        // Pobierz lot lub rzuć wyjątek
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        // Usuń powiązanych pasażerów
        List<Passenger> passengers = passengerRepository.findByFlightId(flightId);
        passengerRepository.deleteAll(passengers);

        // Usuń lot
        flightRepository.delete(flight);

        return ResponseEntity.ok("Flight and associated passengers deleted successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
}
}