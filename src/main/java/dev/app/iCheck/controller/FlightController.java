// package dev.app.iCheck.controller;

// import dev.app.iCheck.model.Flight;
// import dev.app.iCheck.repository.DestinationRepository;
// import dev.app.iCheck.repository.FlightRepository;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// import javax.print.attribute.standard.Destination;

// @RestController
// @RequestMapping("/api/flights")
// public class FlightController {

//     private final FlightRepository flightRepository;

//     @Autowired
//     private DestinationRepository destinationRepository;

//     public FlightController(FlightRepository flightRepository) {
//         this.flightRepository = flightRepository;
//     }

//     // Metoda do dodawania nowego lotu
//     @PostMapping
//     public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
//         Flight savedFlight = flightRepository.save(flight);
//         return ResponseEntity.ok(savedFlight);
//     }

//     // Metoda do pobierania wszystkich lotów
//     @GetMapping
//     public ResponseEntity<List<Flight>> getFlights() {
//         List<Flight> flights = flightRepository.findAll(); // Pobiera wszystkie loty z bazy
//         return ResponseEntity.ok(flights);
//     }
// }

package dev.app.iCheck.controller;

import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Destination;
import dev.app.iCheck.repository.DestinationRepository;
import dev.app.iCheck.repository.FlightRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @PostMapping
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        try {
            // Logowanie otrzymanych danych lotu
            System.out.println("Received flight: " + flight);

            String[] routeParts = flight.getRoute().split(" - ");
            if (routeParts.length != 2) {
                return ResponseEntity.badRequest().body("Invalid route format. Expected 'KTW - destinationId'.");
            }

            String destinationCode = routeParts[1]; // ID destynacji, np. "AYT"
            Optional<Destination> destinationOpt = destinationRepository.findById(destinationCode);

            if (destinationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid destination: " + destinationCode);
            }

            // Zapis lotu w bazie danych
            Flight savedFlight = flightRepository.save(flight);
            return ResponseEntity.ok(savedFlight);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

/*
    // Metoda do dodawania nowego lotu
    @PostMapping
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        // Walidacja destynacji na podstawie kodu w trasie
        String destinationCode = flight.getRoute().split(" - ")[1];
        Optional<Destination> destinationOpt = destinationRepository.findById(destinationCode);
        if (destinationOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid destination provided: " + destinationCode);
        }

        // Zapis lotu w bazie
        Flight savedFlight = flightRepository.save(flight);
        return ResponseEntity.ok(savedFlight);
    }
 */


    // Metoda do pobierania wszystkich lotów
    @GetMapping
    public ResponseEntity<List<Flight>> getFlights() {
        List<Flight> flights = flightRepository.findAll();
        return ResponseEntity.ok(flights);
    }

    // Metoda do pobierania lotu po ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable String id) {
        Optional<Flight> flightOpt = flightRepository.findById(id);
        if (flightOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flightOpt.get());
    }

    // Metoda do usuwania lotu po ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable String id) {
        if (!flightRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        flightRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/destinations")
    public ResponseEntity<List<Destination>> getDestinations() {
        List<Destination> destinations = destinationRepository.findAll();
        System.out.println("Destinations: " + destinations); // Logowanie danych
        return ResponseEntity.ok(destinations);
    }
}
