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

/**
 * Controller class for managing flight-related operations.
 * Provides endpoints for adding, retrieving, and updating flights, as well as managing seat assignments.
 */
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

    /**
     * Adds a new flight.
     *
     * @param flight The Flight object to add.
     * @return ResponseEntity with the added flight or an error message.
     */
    @PostMapping("/add-flight")
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        try {
            System.out.println("Received flight: " + flight);
            // Check for unique flight number
            Optional<Flight> existingFlight = flightRepository.findByFlightNumber(flight.getFlightNumber());
            if (existingFlight.isPresent()) {
                return ResponseEntity.badRequest().body("Flight number already exists: " + flight.getFlightNumber());
            }

            // Validate route format
            String[] routeParts = flight.getRoute().split(" - ");
            if (routeParts.length != 2) {
                return ResponseEntity.badRequest().body("Invalid route format. Expected 'KTW - DEST_ID'.");
            }

            // Validate destination
            String destinationCode = routeParts[1];
            Optional<Destination> destinationOpt = destinationRepository.findById(destinationCode);
            if (destinationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Destination code not found: " + destinationCode);
            }

            // Validate plane
            if (flight.getPlaneId() == null || flight.getPlaneId().isEmpty()) {
                return ResponseEntity.badRequest().body("Plane ID is required.");
            }

            Optional<Plane> planeOpt = planeRepository.findById(flight.getPlaneId());
            if (planeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Plane not found: " + flight.getPlaneId());
            }

            // Find aircraft based on Aircraft ID
            Optional<Aircraft> aircraftOpt = aircraftRepository.findById(flight.getAircraftId());
            if (aircraftOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aircraft not found");
            }

            // Copy seatMap as a flat list of strings
List<String> copiedSeatMap = new ArrayList<>(planeOpt.get().getSeatMap());

// Create a new Flight object with the corrected seatMap structure
Flight newFlight = new Flight(
        flight.getId(),
        flight.getFlightNumber(),
        flight.getRoute(),
        flight.getStatus(),
        flight.getDepartureDate(),
        flight.getDepartureTime(),
        flight.getAircraftId(),
        flight.getPlaneId(),
        copiedSeatMap, // Corrected seatMap passing
        flight.getOccupiedSeats(),
        flight.getPassengers());

// Set default status
newFlight.setStatus("Prepare");

// Save the new flight to the database
flightRepository.save(newFlight);

return ResponseEntity.ok(newFlight);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while saving flight: " + e.getMessage());
        }
    }

    /**
     * Retrieves all flights, optionally filtered by date.
     *
     * @param date The departure date to filter flights by (optional).
     * @return ResponseEntity with a list of flights.
     */
    @GetMapping
    public ResponseEntity<?> getFlights(@RequestParam(required = false) String date) {
        try {
            List<Flight> flights = (date != null && !date.isEmpty())
                    ? flightRepository.findByDepartureDate(date)
                    : flightRepository.findAll();

            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves the seat map for a specific flight.
     *
     * @param flightId The ID of the flight.
     * @return ResponseEntity with the seat map or an error message.
     */
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

    /**
     * Retrieves passengers for a specific flight.
     *
     * @param flightId The ID of the flight.
     * @return ResponseEntity with a list of passengers or an error message.
     */
    @GetMapping("/{flightId}/passengers")
    public ResponseEntity<?> getPassengersByFlightId(@PathVariable String flightId) {
        try {
            List<Passenger> passengers = passengerRepository.findByFlightId(flightId);
            return ResponseEntity.ok(passengers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Updates the status of a flight.
     *
     * @param flightId  The ID of the flight to update.
     * @param body      A map containing the new status.
     * @return ResponseEntity indicating the success or failure of the status update.
     */
    @PutMapping("/{flightId}/status")
    public ResponseEntity<?> updateFlightStatus(@PathVariable String flightId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("newStatus");

        try {
            // Validate status
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

    /**
     * Retrieves details of a specific flight by ID.
     *
     * @param id The ID of the flight to retrieve.
     * @return ResponseEntity with the flight details or an error message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable String id) {
        try {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

            Map<String, Object> flightDetails = new HashMap<>();
            flightDetails.put("id", flight.getId());
            flightDetails.put("flightNumber", flight.getFlightNumber());
            flightDetails.put("route", flight.getRoute());
            flightDetails.put("status", flight.getStatus()); // Changed to status
            flightDetails.put("departureTime", flight.getDepartureTime());
            flightDetails.put("seatMap", flight.getSeatMap());
            flightDetails.put("occupiedSeats", flight.getOccupiedSeats());
            flightDetails.put("planeId", flight.getPlaneId());

            return ResponseEntity.ok(flightDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while fetching flight: " + e.getMessage());
        }
    }

   /**
    * Assigns a seat to a passenger for a specific flight.
    *
    * @param request The SeatAssignmentRequest containing flight ID, passenger ID, and seat number.
    * @return ResponseEntity indicating the success or failure of the seat assignment.
    */
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

    /**
     * Retrieves the list of occupied seats for a specific flight.
     *
     * @param flightId The ID of the flight.
     * @return A list of occupied seat numbers.
     */
    @GetMapping("/{flightId}/occupied-seats")
    public List<String> getOccupiedSeats(@PathVariable String flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        return flightOpt.map(Flight::getOccupiedSeats).orElse(Collections.emptyList());
    }

    /**
     * Releases a seat assigned to a passenger for a specific flight.
     *
     * @param request The SeatAssignmentRequest containing flight ID, passenger ID, and seat number.
     * @return ResponseEntity indicating the success or failure of the seat release.
     */
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
