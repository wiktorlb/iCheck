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
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
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

            passengers.add(new Passenger(null, flightId, name, surname, gender, status, title));
        }
        return passengers;
    }


/* @PutMapping("/{passengerId}")
public ResponseEntity<?> updatePassenger(@PathVariable("passengerId") String passengerId,
        @RequestBody PassengerAPI updatedPassengerAPI) {
    try {
        // Sprawdź czy ID nie jest null lub puste
        if (passengerId == null || passengerId.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passenger ID cannot be null or empty");
        }

        // Sprawdź, czy pasażer istnieje w bazie
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

        // Sprawdź czy updatedPassengerAPI nie jest null
        if (updatedPassengerAPI == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Updated passenger data cannot be null");
        }

        // Reszta kodu bez zmian
        PassengerAPI passengerAPI = new PassengerAPI(
                passenger.getId(),
                passenger.getFlightId(),
                updatedPassengerAPI.getName() != null ? updatedPassengerAPI.getName() : passenger.getName(),
                updatedPassengerAPI.getSurname() != null ? updatedPassengerAPI.getSurname() : passenger.getSurname(),
                updatedPassengerAPI.getGender() != null ? updatedPassengerAPI.getGender() : passenger.getGender(),
                updatedPassengerAPI.getStatus() != null ? updatedPassengerAPI.getStatus() : passenger.getStatus(),
                updatedPassengerAPI.getTitle() != null ? updatedPassengerAPI.getTitle() : passenger.getTitle(),
                updatedPassengerAPI.getDateOfBirth(),
                updatedPassengerAPI.getCitizenship(),
                updatedPassengerAPI.getDocumentType(),
                updatedPassengerAPI.getSerialName(),
                updatedPassengerAPI.getValidUntil(),
                updatedPassengerAPI.getIssueCountry());

        passengerRepository.save(passengerAPI);
        return ResponseEntity.ok(passengerAPI);

    } catch (ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating passenger: " + e.getMessage());
    }
} */

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

        // Aktualizuj dane podstawowe
        existingPassenger.setName(updatedPassengerAPI.getName());
        existingPassenger.setSurname(updatedPassengerAPI.getSurname());
        existingPassenger.setGender(updatedPassengerAPI.getGender());
        existingPassenger.setStatus(updatedPassengerAPI.getStatus());
        existingPassenger.setTitle(updatedPassengerAPI.getTitle());

        // Stwórz nowy obiekt PassengerAPI z zaktualizowanymi danymi
        PassengerAPI passengerAPI = new PassengerAPI(
                existingPassenger.getId(),
                existingPassenger.getFlightId(),
                updatedPassengerAPI.getName(),
                updatedPassengerAPI.getSurname(),
                updatedPassengerAPI.getGender(),
                updatedPassengerAPI.getStatus(),
                updatedPassengerAPI.getTitle(),
                updatedPassengerAPI.getDateOfBirth(),
                updatedPassengerAPI.getCitizenship(),
                updatedPassengerAPI.getDocumentType(),
                updatedPassengerAPI.getSerialName(),
                updatedPassengerAPI.getValidUntil(),
                updatedPassengerAPI.getIssueCountry());

        // Zapisz zaktualizowanego pasażera
        PassengerAPI savedPassenger = passengerRepository.save(passengerAPI);

        return ResponseEntity.ok(savedPassenger);
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

        // Dodaj kody SRR do odpowiedzi
        Map<String, Object> response = new HashMap<>();
        response.put("passenger", passenger);
        response.put("srrCodes", passenger.getSRRCodes());

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching passenger: " + e.getMessage());
    }
}

@GetMapping("/flights/{flightId}/passengers-with-srr")
public ResponseEntity<?> getPassengersWithSrr(@PathVariable String flightId) {
    try {
        List<Passenger> passengers = passengerRepository.findByFlightId(flightId);

        // Przygotuj listę pasażerów z kodami SSR
        List<Map<String, Object>> passengersWithSrr = passengers.stream()
            .map(passenger -> {
                Map<String, Object> passengerData = new HashMap<>();
                passengerData.put("id", passenger.getId());
                passengerData.put("name", passenger.getName());
                passengerData.put("surname", passenger.getSurname());
                passengerData.put("gender", passenger.getGender());
                passengerData.put("status", passenger.getStatus());
                passengerData.put("title", passenger.getTitle());
                passengerData.put("srrCodes", passenger.getSRRCodes());

                // Dodaj informacje o bagażu i komentarzach, jeśli są potrzebne
                passengerData.put("baggageList", passenger.getBaggageList());
                passengerData.put("comments", passenger.getComments());

                return passengerData;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(passengersWithSrr);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error fetching passengers: " + e.getMessage());
    }
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
}
