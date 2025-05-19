package dev.app.iCheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Aircraft;
import dev.app.iCheck.model.Destination;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.AircraftRepository;
import dev.app.iCheck.repository.DestinationRepository;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.repository.PlaneRepository;
import dev.app.iCheck.service.FlightService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private PlaneRepository planeRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    public FlightController(FlightRepository flightRepository, AircraftRepository aircraftRepository,
            PlaneRepository planeRepository) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.planeRepository = planeRepository;
    }

    // Endpoint do dodawania lotów
    @PostMapping("/add-flight")
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        try {
            System.out.println("Received flight: " + flight); // Dodaj logowanie
            // Sprawdzenie unikalności numeru lotu
            Optional<Flight> existingFlight = flightRepository.findByFlightNumber(flight.getFlightNumber());
            if (existingFlight.isPresent()) {
                return ResponseEntity.badRequest().body("Flight number already exists: " + flight.getFlightNumber());
            }

            // Sprawdzenie poprawności trasy
            String[] routeParts = flight.getRoute().split(" - ");
            if (routeParts.length != 2) {
                return ResponseEntity.badRequest().body("Invalid route format. Expected 'KTW - DEST_ID'.");
            }

            // Walidacja miejsca docelowego
            String destinationCode = routeParts[1];
            Optional<Destination> destinationOpt = destinationRepository.findById(destinationCode);
            if (destinationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Destination code not found: " + destinationCode);
            }

            // Walidacja samolotu
            if (flight.getPlaneId() == null || flight.getPlaneId().isEmpty()) {
                return ResponseEntity.badRequest().body("Plane ID is required.");
            }

            Optional<Plane> planeOpt = planeRepository.findById(flight.getPlaneId());
            if (planeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Plane not found: " + flight.getPlaneId());
            }

            // Znajdź samolot na podstawie ID samolotu w Aircraft
            Optional<Aircraft> aircraftOpt = aircraftRepository.findById(flight.getAircraftId());
            if (aircraftOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aircraft not found");
            }


            // Skopiowanie seatMap jako płaskiej listy stringów
List<String> copiedSeatMap = new ArrayList<>(planeOpt.get().getSeatMap());

// Utworzenie nowego obiektu Flight z poprawioną strukturą seatMap
Flight newFlight = new Flight(
        flight.getId(),
        flight.getFlightNumber(),
        flight.getRoute(),
        flight.getStatus(),
        flight.getDepartureDate(),
        flight.getDepartureTime(),
        flight.getAircraftId(),
        flight.getPlaneId(),
        copiedSeatMap, // Poprawione przekazywanie seatMap
        flight.getOccupiedSeats(),
        flight.getPassengers());

// Ustawienie domyślnego statusu
newFlight.setStatus("Prepare");

// Zapisanie nowego lotu do bazy
flightRepository.save(newFlight);

return ResponseEntity.ok(newFlight);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while saving flight: " + e.getMessage());
        }
    }

    // Endpoint do pobierania wszystkich lotów
    @GetMapping
    public ResponseEntity<?> getFlights(@RequestParam(required = false) String date) {
        try {
            List<Flight> flights = (date != null && !date.isEmpty())
                    ? flightRepository.findByDepartureDate(date)
                    : flightRepository.findAll();

            return ResponseEntity.ok(flights); // Zwracaj zawsze listę
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Endpoint do pobierania mapy miejsc danego lotu
    @GetMapping("/{flightId}/seatmap")
    public ResponseEntity<?> getSeatMap(@PathVariable String flightId) {
        try {
            Flight flight = flightRepository.findById(flightId)
                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

            return ResponseEntity.ok(flight.getSeatMap());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching seat map: " + e.getMessage());
        }
    }

    // Endpoint do pobierania pasażerów danego lotu
    @GetMapping("/{flightId}/passengers")
    public ResponseEntity<?> getPassengersByFlightId(@PathVariable String flightId) {
        try {
            List<Passenger> passengers = passengerRepository.findByFlightId(flightId);
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Endpoint do zmiany statusu lotu
    @PutMapping("/{flightId}/status")
    public ResponseEntity<?> updateFlightStatus(@PathVariable String flightId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("newStatus");

        try {
            // Sprawdzenie poprawności statusu
            Flight.FlightStatus status = Flight.FlightStatus.valueOf(newStatus.toUpperCase());

            Flight flight = flightRepository.findById(flightId)
                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

            flight.setStatus(newStatus);
            flightRepository.save(flight);

            return ResponseEntity.ok("Flight status updated to: " + newStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid status. Allowed values: PREPARE, OPEN, CLOSED, FINALIZED.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating flight status: " + e.getMessage());
        }
    }

    // Endpoint do pobierania szczegółów lotu
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable String id) {
        try {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

            Map<String, Object> flightDetails = new HashMap<>();
            flightDetails.put("id", flight.getId());
            flightDetails.put("flightNumber", flight.getFlightNumber());
            flightDetails.put("route", flight.getRoute());
            flightDetails.put("status", flight.getStatus()); // Zmieniono na status
            flightDetails.put("departureTime", flight.getDepartureTime());
            flightDetails.put("seatMap", flight.getSeatMap());
            flightDetails.put("occupiedSeats", flight.getOccupiedSeats());
            flightDetails.put("planeId", flight.getPlaneId());

            return ResponseEntity.ok(flightDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while fetching flight: " + e.getMessage());
        }
    }


/*     @PostMapping("/assign-seat")
    public String assignSeat(@RequestBody SeatAssignmentRequest request) {
        return flightService.assignSeat(request.getFlightId(), request.getPassengerId(), request.getSeatNumber());
    } */
   @PostMapping("/assign-seat")
public ResponseEntity<?> assignSeat(@RequestBody SeatAssignmentRequest request) {
    try {
        String result = flightService.assignSeat(request.getFlightId(), request.getPassengerId(),
                request.getSeatNumber());
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error assigning seat: " + e.getMessage());
    }
}

    @GetMapping("/{flightId}/occupied-seats")
    public List<String> getOccupiedSeats(@PathVariable String flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        return flightOpt.map(Flight::getOccupiedSeats).orElse(Collections.emptyList());
    }

    @PostMapping("/release-seat")
    public ResponseEntity<?> releaseSeat(@RequestBody SeatAssignmentRequest request) {
        try {
            String result = flightService.releaseSeat(request.getFlightId(), request.getPassengerId(),
                    request.getSeatNumber());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error releasing seat: " + e.getMessage());
        }
    }
}
