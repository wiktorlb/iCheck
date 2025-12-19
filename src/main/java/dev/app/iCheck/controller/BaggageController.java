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
import dev.app.iCheck.model.Baggage.BaggageType;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.service.BaggageService;
import dev.app.iCheck.service.FlightService;

/**
 * Controller for managing baggage for passengers.
 * Provides endpoints for adding baggage to a passenger.
 */
@RestController
@RequestMapping("/api/passengers")
public class BaggageController {

     @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BaggageService baggageService;

    @Autowired
    private FlightService flightService;


/**
 * Adds a new baggage item to a passenger.
 *
 * @param passengerId The ID of the passenger to add baggage to.
 * @param baggage     The Baggage object to add.
 * @return ResponseEntity with the updated passenger or a not found status.
 */
@PostMapping("/{passengerId}/add-baggage")
@PutMapping("/{passengerId}/add-baggage")
public ResponseEntity<?> addBaggage(@PathVariable String passengerId, @RequestBody BaggageRequest baggageRequest) {
    if (baggageRequest == null || baggageRequest.getBaggageType() == null || baggageRequest.getBaggageWeight() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Baggage type and weight are required");
    }

    try {
        Baggage baggage = new Baggage();
        baggage.setId(baggageService.generateBaggageId());
        baggage.setWeight(baggageRequest.getBaggageWeight());

        BaggageType type = BaggageType.valueOf(baggageRequest.getBaggageType().toUpperCase());
        baggage.setType(type);

        Optional<Passenger> passengerOpt = passengerRepository.findById(passengerId);
        if (passengerOpt.isPresent()) {
            Passenger passenger = passengerOpt.get();
            flightService.requireEditableFlight(passenger.getFlightId());

            if (passenger.getBaggageList() == null) {
                passenger.setBaggageList(new ArrayList<>());
            }

            passenger.getBaggageList().add(baggage);
            passengerRepository.save(passenger);
            return ResponseEntity.ok(passenger);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger not found");
    } catch (IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid baggage type: " + baggageRequest.getBaggageType());
    } catch (IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding baggage: " + e.getMessage());
    }
}

private static class BaggageRequest {
    private String baggageType;
    private Double baggageWeight;

    public String getBaggageType() {
        return baggageType;
    }

    public void setBaggageType(String baggageType) {
        this.baggageType = baggageType;
    }

    public Double getBaggageWeight() {
        return baggageWeight;
    }

    public void setBaggageWeight(Double baggageWeight) {
        this.baggageWeight = baggageWeight;
    }
}
}
