package dev.app.iCheck.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
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

            passengers.add(new Passenger(null, flightId, name, surname, gender));
        }
        return passengers;
    }

/*     @DeleteMapping("/{flightId}/passengers/{passengerId}")
    public ResponseEntity<?> deletePassenger(
            @PathVariable String flightId,
            @PathVariable String passengerId) {
        try {
            System.out.println("Searching for flight");
            Flight flight = flightRepository.findById(flightId)
                    .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
            Passenger passenger = passengerRepository.findById(passengerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

            if (!flight.getPassengers().contains(passenger)) {
                throw new ResourceNotFoundException("Passenger not associated with this flight");
            }

            flight.getPassengers().remove(passenger);
            flightRepository.save(flight);
            passengerRepository.delete(passenger);

            return ResponseEntity.ok("Passenger deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    } */
}
