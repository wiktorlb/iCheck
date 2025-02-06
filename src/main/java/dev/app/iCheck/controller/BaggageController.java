package dev.app.iCheck.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.app.iCheck.model.Baggage;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.service.BaggageService;
import dev.app.iCheck.service.FlightService;

@RestController
@RequestMapping("/api/passengers")
public class BaggageController {

     @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BaggageService baggageService;


@PutMapping("/{passengerId}/add-baggage")
public ResponseEntity<?> addBaggage(@PathVariable String passengerId, @RequestBody Baggage baggage) {
    String baggageId = baggageService.generateBaggageId();
    baggage.setId(baggageId);

    Optional<Passenger> passengerOpt = passengerRepository.findById(passengerId);
    if (passengerOpt.isPresent()) {
        Passenger passenger = passengerOpt.get();

        // 🔹 Zapewniamy, że lista nie jest null
        if (passenger.getBaggageList() == null) {
            passenger.setBaggageList(new ArrayList<>());
        }

        passenger.getBaggageList().add(baggage);
        passengerRepository.save(passenger);
        return ResponseEntity.ok(passenger);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger not found");
}
}
