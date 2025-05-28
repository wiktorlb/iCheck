package dev.app.iCheck.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* import dev.app.iCheck.exception.GlobalExceptionHandler.ResourceNotFoundException; */
import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.repository.PlaneRepository;

/**
 * Service class for managing flight-related operations.
 * Handles business logic for flights, including passenger management and seat assignments.
 */
@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PlaneRepository planeRepository;

    /**
     * Retrieves all passengers for a specific flight.
     *
     * @param flightId The ID of the flight
     * @return List of passengers on the flight
     * @throws RuntimeException if the flight is not found
     */
    public List<Passenger> getPassengersByFlightId(String flightId) {
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            return flight.get().getPassengers(); // Zakładamy, że lot ma listę pasażerów
        } else {
            throw new RuntimeException("Flight not found");
        }
    }

    /**
     * Deletes a passenger from a flight.
     *
     * @param flightId The ID of the flight
     * @param passengerId The ID of the passenger to delete
     * @throws ResourceNotFoundException if the flight or passenger is not found
     */
    public void deletePassenger(String flightId, String passengerId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        if (!flight.getPassengers().contains(passenger)) {
            throw new ResourceNotFoundException("Passenger not found in this flight");
        }

        flight.getPassengers().remove(passenger);

        passengerRepository.delete(passenger);
        System.out.println("Passenger deleted from database");
    }

    /**
     * Assigns a seat to a passenger on a flight.
     *
     * @param flightId The ID of the flight
     * @param passengerId The ID of the passenger
     * @param seatNumber The seat number to assign
     * @return A message indicating the result of the operation
     * @throws ResourceNotFoundException if the flight or passenger is not found
     * @throws IllegalStateException if the seat is already occupied
     * @throws IllegalArgumentException if the seat number is invalid
     */
    public String assignSeat(String flightId, String passengerId, String seatNumber) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightId));

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

        if (flight.isSeatOccupied(seatNumber)) {
            throw new IllegalStateException("Miejsce " + seatNumber + " jest już zajęte");
        }

        boolean seatExists = false;
        for (String row : flight.getSeatMap()) {
            if (row.contains(seatNumber)) {
                seatExists = true;
                break;
            }
        }
        if (!seatExists) {
            throw new IllegalArgumentException("Nieprawidłowy numer miejsca: " + seatNumber);
        }

        passenger.setSeatNumber(seatNumber);
        passengerRepository.save(passenger);

        flight.addSeat(seatNumber);
        flightRepository.save(flight);

        return "Miejsce " + seatNumber + " zostało przypisane do pasażera " + passenger.getName();
    }

    /**
     * Releases a seat assignment for a passenger on a flight.
     *
     * @param flightId The ID of the flight
     * @param passengerId The ID of the passenger
     * @param seatNumber The seat number to release
     * @return A message indicating the result of the operation
     * @throws ResourceNotFoundException if the flight or passenger is not found
     * @throws IllegalArgumentException if the passenger does not have the seat assigned
     */
    public String releaseSeat(String flightId, String passengerId, String seatNumber) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightId));

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

        if (!seatNumber.equals(passenger.getSeatNumber())) {
            throw new IllegalArgumentException("Passenger does not have this seat assigned");
        }

        passenger.setSeatNumber(null);
        passengerRepository.save(passenger);

        List<String> occupiedSeats = flight.getOccupiedSeats();
        if (occupiedSeats != null) {
            occupiedSeats.remove(seatNumber);
            flight.setOccupiedSeats(occupiedSeats);
            flightRepository.save(flight);
        }

        return "Seat " + seatNumber + " has been released for passenger " + passengerId;
    }
}
