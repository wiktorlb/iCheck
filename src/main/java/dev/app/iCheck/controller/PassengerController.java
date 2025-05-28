package dev.app.iCheck.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Baggage;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.model.Passenger.Comment;
import dev.app.iCheck.model.PassengerAPI;
import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.repository.PlaneRepository;
import dev.app.iCheck.service.FlightService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Controller class for managing passenger-related operations.
 * Handles requests for uploading, updating, retrieving, and managing passengers for flights.
 */
@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightRepository flightRepository;

    /**
     * Uploads a list of passengers from a file for a specific flight.
     *
     * @param flightId The ID of the flight to add passengers to.
     * @param file     The multipart file containing passenger data.
     * @return ResponseEntity indicating the success or failure of the upload.
     */
    @PostMapping("/{flightId}/upload")
    public ResponseEntity<?> uploadPassengers(@PathVariable("flightId") String flightId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Process the text file
            List<Passenger> passengers = parseFile(file, flightId);

            // Save passengers to the database
            passengerRepository.saveAll(passengers);

            return ResponseEntity.ok("Passengers successfully added to flight with ID: " + flightId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Parses a multipart file containing passenger data and returns a list of Passenger objects.
     *
     * @param file     The multipart file to parse.
     * @param flightId The ID of the flight the passengers belong to.
     * @return A list of Passenger objects created from the file data.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    private List<Passenger> parseFile(MultipartFile file, String flightId) throws IOException {
        List<Passenger> passengers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            // Process each line
            String[] parts = line.trim().split("\s+"); // Split by spaces, handling multiple spaces
            if (parts.length < 3)
                continue; // Skip invalid lines

            // Gender is always the last element
            String rawGender = parts[parts.length - 1].toUpperCase();
            String gender = rawGender.equals("FEMALE") || rawGender.equals("F") ? "F"
                    : rawGender.equals("MALE") || rawGender.equals("M") ? "M" : "F"; // Default to "F" if unknown

            // Determine if second-to-last element is a valid title
            String[] validTitles = { "MR", "MRS", "CHLD" };
            String title = "NONE";
            if (Arrays.asList(validTitles).contains(parts[parts.length - 2])) {
                title = parts[parts.length - 2];
            }

            // Surname is always first element
            String surname = parts[0];

            // Name is everything between surname and title
            int nameEndIndex = title.equals("NONE") ? parts.length - 1 : parts.length - 2;
            String name = String.join(" ", Arrays.copyOfRange(parts, 1, nameEndIndex));
            System.out.println("Reading from file");
System.out.println("-----------------------");
            System.out.println("Name: " + name + ", Surname: " + surname + ", Title: " + title + ", Gender: " + gender);

            String status = "NONE"; // Default status

            passengers.add(new Passenger(null, flightId, name, surname, gender, status, title, null));
        }
        return passengers;
    }

/**
 * Updates the details of an existing passenger.
 *
 * @param passengerId         The ID of the passenger to update.
 * @param updatedPassengerAPI The PassengerAPI object containing the updated passenger data.
 * @return ResponseEntity with the updated passenger data or an error message.
 */
@PutMapping("/{passengerId}")
public ResponseEntity<?> updatePassenger(@PathVariable("passengerId") String passengerId,
        @RequestBody PassengerAPI updatedPassengerAPI) {
    try {
        // Input data validation
        if (passengerId == null || passengerId.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passenger ID cannot be null or empty");
        }

        if (updatedPassengerAPI == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Updated passenger data cannot be null");
        }

        // Check if passenger exists in the database
        Passenger existingPassenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

        // Preserve existing data
        List<Baggage> existingBaggage = existingPassenger.getBaggageList();
        List<Comment> existingComments = existingPassenger.getComments();

        // Create a new PassengerAPI object with updated data
        PassengerAPI passengerAPI = new PassengerAPI(
                existingPassenger.getId(),
                existingPassenger.getFlightId(),
                updatedPassengerAPI.getName(),
                updatedPassengerAPI.getSurname(),
                updatedPassengerAPI.getGender(),
                updatedPassengerAPI.getStatus(),
                updatedPassengerAPI.getTitle(),
                updatedPassengerAPI.getSeatNumber(),
                updatedPassengerAPI.getDateOfBirth(),
                updatedPassengerAPI.getCitizenship(),
                updatedPassengerAPI.getDocumentType(),
                updatedPassengerAPI.getSerialName(),
                updatedPassengerAPI.getValidUntil(),
                updatedPassengerAPI.getIssueCountry());

        // Restore preserved data
        passengerAPI.setBaggageList(existingBaggage);
        passengerAPI.setComments(existingComments);

        // Save the updated passenger
        PassengerAPI savedPassenger = passengerRepository.save(passengerAPI);

        // Fetch fresh data from the database
        Passenger refreshedPassenger = passengerRepository.findById(savedPassenger.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found after save"));

        // Return response with current data
        Map<String, Object> response = new HashMap<>();
        response.put("passenger", refreshedPassenger);
        response.put("srrCodes", refreshedPassenger.getSRRCodes());

        return ResponseEntity.ok(response);

    } catch (ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating passenger: " + e.getMessage());
    }
}

/**
 * Updates the status of an existing passenger.
 *
 * @param passengerId The ID of the passenger to update the status for.
 * @param status      The new status for the passenger.
 * @return ResponseEntity with the updated passenger or an error message.
 */
@PutMapping("/{passengerId}/status")
public ResponseEntity<?> updatePassengerStatus(@PathVariable String passengerId, @RequestBody String status) {
    try {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        passenger.setStatus(status.replaceAll("[\"{}]", "")); // Remove JSON quotes and braces

        passengerRepository.save(passenger);
        return ResponseEntity.ok(passenger);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating passenger status: " + e.getMessage());
    }
}

/**
 * Retrieves the details of a single passenger by ID.
 *
 * @param passengerId The ID of the passenger to retrieve.
 * @return ResponseEntity with the passenger details or an error message.
 */
@GetMapping("/{passengerId}")
public ResponseEntity<?> getPassenger(@PathVariable("passengerId") String passengerId) {
    try {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        // Prepare response with all necessary data
        Map<String, Object> response = new HashMap<>();
        response.put("passenger", passenger);
        response.put("srrCodes", passenger.getSRRCodes());
        response.put("baggageList", passenger.getBaggageList());
        response.put("comments", passenger.getComments());
        response.put("FlightId", passenger.getFlightId());

        return ResponseEntity.ok(response);
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error fetching passenger: " + e.getMessage()));
    }
}

/**
 * Retrieves a list of passengers with SSR details for a given flight.
 *
 * @param flightId The ID of the flight to retrieve passengers for.
 * @return ResponseEntity with a list of passengers and their details, including SSR codes and baggage.
 */
@GetMapping("/flights/{flightId}/passengers-with-srr")
public ResponseEntity<?> getPassengersWithSrr(@PathVariable String flightId) {
    try {
        List<Passenger> passengers = passengerRepository.findByFlightId(flightId);

        List<Map<String, Object>> passengersWithDetails = passengers.stream()
                .map(passenger -> {
                    Map<String, Object> passengerData = new HashMap<>();
                    // Basic data
                    passengerData.put("id", passenger.getId());
                    passengerData.put("name", passenger.getName());
                    passengerData.put("surname", passenger.getSurname());
                    passengerData.put("gender", passenger.getGender());
                    passengerData.put("status", passenger.getStatus());
                    passengerData.put("title", passenger.getTitle());
                    passengerData.put("seatNumber", passenger.getSeatNumber());

                    // Baggage
                    List<Map<String, Object>> baggageDetails = new ArrayList<>();
                    if (passenger.getBaggageList() != null) {
                        for (Baggage baggage : passenger.getBaggageList()) {
                            Map<String, Object> baggageMap = new HashMap<>();
                            baggageMap.put("id", baggage.getId());
                            baggageMap.put("weight", baggage.getWeight());
                            baggageMap.put("type", baggage.getType()); // Add baggage type
                            baggageDetails.add(baggageMap);
                        }
                    }
                    passengerData.put("baggageList", baggageDetails);

                    // Comments
                    passengerData.put("comments", passenger.getComments());

                    // SSR codes
                    passengerData.put("srrCodes", passenger.getSRRCodes());

                    passengerData.put("flightId", passenger.getFlightId());

                    // Document data (if it's PassengerAPI)
                    if (passenger instanceof PassengerAPI) {
                        PassengerAPI papi = (PassengerAPI) passenger;
                        passengerData.put("documentType", papi.getDocumentType());
                        passengerData.put("citizenship", papi.getCitizenship());
                        passengerData.put("serialName", papi.getSerialName());
                        passengerData.put("validUntil", papi.getValidUntil());
                        passengerData.put("issueCountry", papi.getIssueCountry());
                    }

                    return passengerData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(passengersWithDetails);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching passengers: " + e.getMessage());
    }
}

/**
 * Adds an SSR code to a passenger.
 *
 * @param passengerId The ID of the passenger to add the SSR code to.
 * @param request     A map containing the SSR code.
 * @return ResponseEntity indicating the success or failure of the operation.
 */
@PostMapping("/{passengerId}/add-srr-code")
public ResponseEntity<?> addSrrCode(@PathVariable String passengerId, @RequestBody Map<String, String> request) {
    String srrCode = request.get("srrCode");
    // TODO: Implement adding SSR code logic
    return ResponseEntity.ok().build();
}

/**
 * Adds a comment to a passenger.
 *
 * @param passengerId The ID of the passenger to add the comment to.
 * @param newComment  The comment to add.
 * @return ResponseEntity with the updated passenger or a not found status.
 */
@PutMapping("/{id}/add-comment")
public ResponseEntity<Passenger> addComment(@PathVariable("id") String passengerId, @RequestBody Comment newComment) {
    Optional<Passenger> passengerOpt = passengerRepository.findById(passengerId);
    if (passengerOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    Passenger passenger = passengerOpt.get();
    passenger.getComments().add(newComment);
    passengerRepository.save(passenger);

    return ResponseEntity.ok(passenger);
}

/**
 * Assigns a seat to a passenger.
 *
 * @param request The SeatAssignmentRequest containing flight ID, passenger ID, and seat number.
 * @return ResponseEntity indicating the success or failure of the seat assignment.
 */
@PostMapping("{passengerId}/assign-seat")
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
 * Releases a seat assigned to a passenger.
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error releasing seat: " + e.getMessage());
    }
}

/**
 * Boards a list of passengers for a specific flight.
 *
 * @param flightId     The ID of the flight.
 * @param passengerIds A list of passenger IDs to board.
 * @return ResponseEntity with the updated passenger details or an error message.
 */
@PutMapping("/flights/{flightId}/board-passengers")
public ResponseEntity<?> boardPassengers(@PathVariable String flightId, @RequestBody List<String> passengerIds) {
    try {
        // Validate input
        if (passengerIds == null || passengerIds.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passenger IDs list cannot be null or empty");
        }

        // Get all passengers in one query
        List<Passenger> passengers = passengerRepository.findAllById(passengerIds);

        // Validate that all passengers exist and belong to the specified flight
        for (Passenger passenger : passengers) {
            if (!passenger.getFlightId().equals(flightId)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Passenger " + passenger.getId() + " does not belong to flight " + flightId);
            }
        }

        // Update all passengers' status to BOARDED
        passengers.forEach(passenger -> passenger.setStatus("BOARDED"));

        // Save all passengers in one batch operation
        passengerRepository.saveAll(passengers);

        // Return updated passengers with their details
        List<Map<String, Object>> updatedPassengers = passengers.stream()
                .map(passenger -> {
                    Map<String, Object> passengerData = new HashMap<>();
                    passengerData.put("id", passenger.getId());
                    passengerData.put("name", passenger.getName());
                    passengerData.put("surname", passenger.getSurname());
                    passengerData.put("gender", passenger.getGender());
                    passengerData.put("status", passenger.getStatus());
                    passengerData.put("title", passenger.getTitle());
                    passengerData.put("seatNumber", passenger.getSeatNumber());
                    passengerData.put("baggageList", passenger.getBaggageList());
                    passengerData.put("comments", passenger.getComments());
                    passengerData.put("srrCodes", passenger.getSRRCodes());
                    passengerData.put("flightId", passenger.getFlightId());
                    return passengerData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(updatedPassengers);

    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error boarding passengers: " + e.getMessage());
    }
}

/**
 * Retrieves a filtered list of passengers with SSR details for a given flight based on status.
 *
 * @param flightId The ID of the flight to retrieve passengers for.
 * @return ResponseEntity with a filtered list of passengers and their details.
 */
@GetMapping("/flights/{flightId}/passengers-with-srr-filtered")
public ResponseEntity<?> getFilteredPassengersWithSrr(@PathVariable String flightId) {
    try {
        // Get all passengers for the flight
        List<Passenger> allPassengers = passengerRepository.findByFlightId(flightId);

        // Filter passengers by status
        List<Passenger> filteredPassengers = allPassengers.stream()
                .filter(passenger -> {
                    String status = passenger.getStatus();
                    return "ACC".equals(status) || "BOARDED".equals(status) || "STBY".equals(status);
                })
                .collect(Collectors.toList());

        // Transform passengers to response format
        List<Map<String, Object>> passengersWithDetails = filteredPassengers.stream()
                .map(passenger -> {
                    Map<String, Object> passengerData = new HashMap<>();
                    // Basic data
                    passengerData.put("id", passenger.getId());
                    passengerData.put("name", passenger.getName());
                    passengerData.put("surname", passenger.getSurname());
                    passengerData.put("gender", passenger.getGender());
                    passengerData.put("status", passenger.getStatus());
                    passengerData.put("title", passenger.getTitle());
                    passengerData.put("seatNumber", passenger.getSeatNumber());

                    // Baggage
                    List<Map<String, Object>> baggageDetails = new ArrayList<>();
                    if (passenger.getBaggageList() != null) {
                        for (Baggage baggage : passenger.getBaggageList()) {
                            Map<String, Object> baggageMap = new HashMap<>();
                            baggageMap.put("id", baggage.getId());
                            baggageMap.put("weight", baggage.getWeight());
                            baggageMap.put("type", baggage.getType());
                            baggageDetails.add(baggageMap);
                        }
                    }
                    passengerData.put("baggageList", baggageDetails);

                    // Comments
                    passengerData.put("comments", passenger.getComments());

                    // SSR codes
                    passengerData.put("srrCodes", passenger.getSRRCodes());

                    passengerData.put("flightId", passenger.getFlightId());

                    // Document data (if it's PassengerAPI)
                    if (passenger instanceof PassengerAPI) {
                        PassengerAPI papi = (PassengerAPI) passenger;
                        passengerData.put("documentType", papi.getDocumentType());
                        passengerData.put("citizenship", papi.getCitizenship());
                        passengerData.put("serialName", papi.getSerialName());
                        passengerData.put("validUntil", papi.getValidUntil());
                        passengerData.put("issueCountry", papi.getIssueCountry());
                    }

                    return passengerData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(passengersWithDetails);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching passengers: " + e.getMessage());
    }
}
}
