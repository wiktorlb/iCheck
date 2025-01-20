package dev.app.iCheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.app.iCheck.model.Destination;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.repository.DestinationRepository;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.service.FlightService;

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

    // @GetMapping("/{id}/passengers")
    // public ResponseEntity<?> getPassengersByFlightId(@PathVariable String id) {
    //     try {
    //         List<Passenger> passengers = flightService.getPassengersByFlightId(id);
    //         return ResponseEntity.ok(passengers);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching passengers");
    //     }
    // }

    @GetMapping("/api/flights/{flightId}/passengers")
    public ResponseEntity<List<Passenger>> getPassengersByFlightId(@PathVariable String flightId) {
        List<Passenger> passengers = passengerRepository.findByFlightId(flightId);
        if (passengers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(passengers);
    }
}