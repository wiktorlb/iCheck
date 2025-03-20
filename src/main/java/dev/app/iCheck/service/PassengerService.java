package dev.app.iCheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Passenger;
import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PassengerRepository;
import dev.app.iCheck.repository.PlaneRepository;

@Service
public class PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private PlaneRepository planeRepository;

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
