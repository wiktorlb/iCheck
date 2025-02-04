package dev.app.iCheck.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.model.PassengerAPI;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.service.FlightService;

import java.io.IOException;
import java.util.Arrays;

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
            String[] parts = line.trim().split(" ");
            String gender = parts[parts.length - 1].toUpperCase();
            String surname = parts[0];
            String name = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 1));
            String status = "NONE"; // Ustawienie statusu na "NONE"

            passengers.add(new Passenger(null, flightId, name, surname, gender, status));
        }
        return passengers;
    }



@PutMapping("/{passengerId}")
public ResponseEntity<?> updatePassenger(@PathVariable("passengerId") String passengerId,
        @RequestBody PassengerAPI updatedPassengerAPI) {
    try {
        // Sprawdź, czy pasażer istnieje w bazie
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        // Utwórz nowy obiekt PassengerAPI z aktualizowanymi danymi
        PassengerAPI passengerAPI = new PassengerAPI(
                passenger.getId(),
                passenger.getFlightId(),
                updatedPassengerAPI.getName() != null ? updatedPassengerAPI.getName() : passenger.getName(),
                updatedPassengerAPI.getSurname() != null ? updatedPassengerAPI.getSurname() : passenger.getSurname(),
                updatedPassengerAPI.getGender() != null ? updatedPassengerAPI.getGender() : passenger.getGender(),
                updatedPassengerAPI.getStatus() != null ? updatedPassengerAPI.getStatus() : passenger.getStatus(),
                updatedPassengerAPI.getDateOfBirth(),
                updatedPassengerAPI.getCitizenship(),
                updatedPassengerAPI.getDocumentType(),
                updatedPassengerAPI.getSerialName(),
                updatedPassengerAPI.getValidUntil(),
                updatedPassengerAPI.getIssueCountry());

        // Zapisz zmodyfikowanego pasażera w bazie
        passengerRepository.save(passengerAPI);

        return ResponseEntity.ok(passengerAPI); // Zwórć zaktualizowanego pasażera
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

        return ResponseEntity.ok(passenger);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching passenger: " + e.getMessage());
    }
}
}
