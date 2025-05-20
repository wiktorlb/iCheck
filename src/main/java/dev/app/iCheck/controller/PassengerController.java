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

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightRepository flightRepository;

    @PostMapping("/{flightId}/upload")
    public ResponseEntity<?> uploadPassengers(@PathVariable("flightId") String flightId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Przetwarzanie pliku tekstowego
            List<Passenger> passengers = parseFile(file, flightId);

            // Zapis pasażerów do bazy danych
            passengerRepository.saveAll(passengers);

            return ResponseEntity.ok("Pasażerowie zostali pomyślnie dodani do lotu o ID: " + flightId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Wystąpił błąd: " + e.getMessage());
        }
    }

    private List<Passenger> parseFile(MultipartFile file, String flightId) throws IOException {
        List<Passenger> passengers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            // Przetwarzanie każdej linii
            String[] parts = line.trim().split("\\s+"); // Split by spaces, handling multiple spaces
            if (parts.length < 3)
                continue; // Pominięcie niepoprawnych linii

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
            System.out.println("zapis z pliku");
System.out.println("-----------------------");
            System.out.println("Imię: " + name + ", Nazwisko: " + surname + ", Tytuł: " + title + ", Płeć: " + gender);

            String status = "NONE"; // Default status

            passengers.add(new Passenger(null, flightId, name, surname, gender, status, title, null));
        }
        return passengers;
    }

@PutMapping("/{passengerId}")
public ResponseEntity<?> updatePassenger(@PathVariable("passengerId") String passengerId,
        @RequestBody PassengerAPI updatedPassengerAPI) {
    try {
        // Walidacja danych wejściowych
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

        // Sprawdź, czy pasażer istnieje w bazie
        Passenger existingPassenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

        // Zachowaj istniejące dane
        List<Baggage> existingBaggage = existingPassenger.getBaggageList();
        List<Comment> existingComments = existingPassenger.getComments();

        // Stwórz nowy obiekt PassengerAPI z zaktualizowanymi danymi
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

        // Przywróć zachowane dane
        passengerAPI.setBaggageList(existingBaggage);
        passengerAPI.setComments(existingComments);

        // Zapisz zaktualizowanego pasażera
        PassengerAPI savedPassenger = passengerRepository.save(passengerAPI);

        // Pobierz świeże dane z bazy
        Passenger refreshedPassenger = passengerRepository.findById(savedPassenger.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found after save"));

        // Zwróć odpowiedź z aktualnymi danymi
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

@PutMapping("/{passengerId}/status")
public ResponseEntity<?> updatePassengerStatus(@PathVariable String passengerId, @RequestBody String status) {
    try {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        passenger.setStatus(status.replaceAll("[\"{}]", "")); // Usuwa cudzysłowy i klamry JSON

        passengerRepository.save(passenger);
        return ResponseEntity.ok(passenger);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating passenger status: " + e.getMessage());
    }
}

@GetMapping("/{passengerId}")
public ResponseEntity<?> getPassenger(@PathVariable("passengerId") String passengerId) {
    try {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        // Przygotuj odpowiedź z wszystkimi potrzebnymi danymi
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

@GetMapping("/flights/{flightId}/passengers-with-srr")
public ResponseEntity<?> getPassengersWithSrr(@PathVariable String flightId) {
    try {
        List<Passenger> passengers = passengerRepository.findByFlightId(flightId);

        List<Map<String, Object>> passengersWithDetails = passengers.stream()
                .map(passenger -> {
                    Map<String, Object> passengerData = new HashMap<>();
                    // Podstawowe dane
                    passengerData.put("id", passenger.getId());
                    passengerData.put("name", passenger.getName());
                    passengerData.put("surname", passenger.getSurname());
                    passengerData.put("gender", passenger.getGender());
                    passengerData.put("status", passenger.getStatus());
                    passengerData.put("title", passenger.getTitle());
                    passengerData.put("seatNumber", passenger.getSeatNumber());

                    // Bagaże
                    List<Map<String, Object>> baggageDetails = new ArrayList<>();
                    if (passenger.getBaggageList() != null) {
                        for (Baggage baggage : passenger.getBaggageList()) {
                            Map<String, Object> baggageMap = new HashMap<>();
                            baggageMap.put("id", baggage.getId());
                            baggageMap.put("weight", baggage.getWeight());
                            baggageMap.put("type", baggage.getType()); // Dodajemy typ bagażu
                            baggageDetails.add(baggageMap);
                        }
                    }
                    passengerData.put("baggageList", baggageDetails);

                    // Komentarze
                    passengerData.put("comments", passenger.getComments());

                    // SSR kody
                    passengerData.put("srrCodes", passenger.getSRRCodes());

                    passengerData.put("flightId", passenger.getFlightId());

                    // Dane dokumentów (jeśli to PassengerAPI)
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

@PostMapping("/{passengerId}/add-srr-code")
public ResponseEntity<?> addSrrCode(@PathVariable String passengerId, @RequestBody Map<String, String> request) {
    String srrCode = request.get("srrCode");
    // Implementacja dodawania kodu SSR
    return ResponseEntity.ok().build();
}

// Nowa metoda do aktualizacji komentarza
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
}
