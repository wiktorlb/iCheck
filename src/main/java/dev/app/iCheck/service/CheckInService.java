package dev.app.iCheck.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Aircraft;
import dev.app.iCheck.model.Flight;
import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.AircraftRepository;
import dev.app.iCheck.repository.FlightRepository;
import dev.app.iCheck.repository.PlaneRepository;

/**
 * Service class for handling check-in operations.
 * Manages flight check-in process and seat assignments.
 */
@Service
public class CheckInService {
    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final PlaneRepository planeRepository;

    public CheckInService(FlightRepository flightRepository, AircraftRepository aircraftRepository,
            PlaneRepository planeRepository) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.planeRepository = planeRepository;
    }

    /**
     * Assigns a seat to a flight.
     * Finds the first available seat in the aircraft's seat map and assigns it.
     *
     * @param flightId The ID of the flight
     * @return The assigned seat number
     * @throws RuntimeException if the flight, aircraft, or plane model is not found, or if no seats are available
     */
    public String assignSeat(String flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Lot nie znaleziony"));

        Aircraft aircraft = aircraftRepository.findById(flight.getAircraftId())
                .orElseThrow(() -> new RuntimeException("Samolot nie znaleziony"));
        Plane plane = planeRepository.findById(aircraft.getPlaneId())
                .orElseThrow(() -> new RuntimeException("Model samolotu nie znaleziony"));

        List<String> seatMap = plane.getFlatSeatMap();
        List<String> occupiedSeats = flight.getOccupiedSeats();

        for (String seat : seatMap) {
            if (!occupiedSeats.contains(seat)) {
                flight.addSeat(seat);
                plane.assignSeat(seat);
                flightRepository.save(flight);
                planeRepository.save(plane);
                return seat;
            }
        }

        throw new RuntimeException("Brak dostępnych miejsc w tym locie!");
    }
}
