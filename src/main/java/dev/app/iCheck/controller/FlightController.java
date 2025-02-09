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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // Walidacja numeru lotu (unikalność)
        Optional<Flight> existingFlight = flightRepository.findByFlightNumber(flight.getFlightNumber());
        if (existingFlight.isPresent()) {
            return ResponseEntity.badRequest().body("Flight number already exists: " + flight.getFlightNumber());
        }

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
           for (Passenger p : passengers) {
            System.out.println("-----------------------------");
               System.out.println("Passenger: " + p.getName() + " " + p.getSurname() +
                       " | Title: " + p.getTitle() + " | Status: " + p.getStatus());
           }
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

@PutMapping("/{flightId}/passengers/{passengerId}/accept")
public ResponseEntity<?> acceptPassenger(@PathVariable("flightId") String flightId,
        @PathVariable("passengerId") String passengerId) {
    try {
        // Pobierz lot z bazy danych
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

        // Pobierz pasażera z bazy danych
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + passengerId));

        // Zmieniamy status pasażera na "Accepted"
        passenger.setStatus("ACC");

        // Zapisujemy zmiany w bazie danych
        passengerRepository.save(passenger);

        return ResponseEntity.ok("Passenger status updated to Accepted");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating passenger status: " + e.getMessage());
    }
}


/*


 */
// Enum statusu lotu
public enum FlightStatus {
    PREPARE, OPEN, CLOSED, FINALIZED
}

@PutMapping("/{flightId}/status")
public ResponseEntity<?> updateFlightStatus(@PathVariable String flightId, @RequestBody Map<String, String> body) {
    String newStatus = body.get("newStatus");

    try {
        // Walidacja statusu
        FlightStatus status = FlightStatus.valueOf(newStatus.toUpperCase());
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Invalid status. Allowed values: prepare, open, closed, finalized.");
    }

    try {
        // Pobierz lot z bazy danych
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

        // Zaktualizuj status lotu
        flight.setState(newStatus);
        flightRepository.save(flight);

        return ResponseEntity.ok("Flight status updated to: " + newStatus);
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating flight status: " + e.getMessage());
    }
}

@GetMapping("/{id}")
public ResponseEntity<?> getFlightById(@PathVariable String id) {
    try {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        Map<String, Object> flightDetails = new HashMap<>();
        flightDetails.put("id", flight.getId());
        flightDetails.put("flightNumber", flight.getFlightNumber());
        flightDetails.put("departureTime", flight.getDepartureTime());
        flightDetails.put("route", flight.getRoute());
        flightDetails.put("state", flight.getState());

        return ResponseEntity.ok(flightDetails);
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching flight: " + e.getMessage());
    }
}
}