package dev.app.iCheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.repository.PlaneRepository;

/**
 * Service class for managing passenger-related operations.
 * Handles passenger creation and seat assignments.
 */
@Service
public class PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private PlaneRepository planeRepository;

    /**
     * Adds a passenger to a plane.
     *
     * @param passenger The passenger to add
     * @param planeId The ID of the plane to add the passenger to
     * @return The added passenger
     * @throws Exception if the plane doesn't exist or the seat is already occupied
     */
    public Passenger addPassenger(Passenger passenger, String planeId) throws Exception {
        Plane plane = planeRepository.findById(planeId).orElseThrow(() -> new Exception("Samolot nie istnieje"));

        if (!plane.isSeatAvailable(passenger.getSeatNumber())) {
            throw new Exception("Miejsce " + passenger.getSeatNumber() + " jest już zajęte!");
        }

        plane.assignSeat(passenger.getSeatNumber());
        planeRepository.save(plane);

        return passengerRepository.save(passenger);
    }
}
