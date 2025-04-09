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

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PlaneRepository planeRepository;

    public List<Passenger> getPassengersByFlightId(String flightId) {
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            return flight.get().getPassengers(); // Zakładamy, że lot ma listę pasażerów
        } else {
            throw new RuntimeException("Flight not found");
        }
    }

    public void deletePassenger(String flightId, String passengerId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        // Sprawdź, czy pasażer jest przypisany do tego lotu
        if (!flight.getPassengers().contains(passenger)) {
            throw new ResourceNotFoundException("Passenger not found in this flight");
        }

        // Usuń pasażera z listy pasażerów w locie
        flight.getPassengers().remove(passenger);

        /* // Zapisz zaktualizowany lot (jeśli to konieczne)
        flightRepository.save(flight); */

        // Usuń pasażera z bazy danych
        passengerRepository.delete(passenger);
        System.out.println("Passenger deleted from database");
    }

 /*    public String assignSeat(String flightId, String passengerId, String seatNumber) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        Optional<Passenger> passengerOpt = passengerRepository.findById(passengerId);

        if (flightOpt.isEmpty() || passengerOpt.isEmpty()) {
            return "Lot lub pasażer nie istnieje.";
        }

        Flight flight = flightOpt.get();
        Passenger passenger = passengerOpt.get();

        if (flight.isSeatOccupied(seatNumber)) {
            return "Miejsce " + seatNumber + " jest już zajęte.";
        }

        flight.addSeat(seatNumber);
        flight.getPassengers().add(passenger);
        flightRepository.save(flight);
        return "Pasażer " + passenger.getName() + " przypisany do miejsca " + seatNumber + " w locie "
                + flight.getFlightNumber();
    } */

 public String assignSeat(String flightId, String passengerId, String seatNumber) {
     Flight flight = flightRepository.findById(flightId)
             .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightId));

     Passenger passenger = passengerRepository.findById(passengerId)
             .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

     // Sprawdź, czy miejsce jest dostępne
     if (flight.isSeatOccupied(seatNumber)) {
         throw new IllegalStateException("Miejsce " + seatNumber + " jest już zajęte");
     }

     // Sprawdź, czy miejsce istnieje w mapie miejsc
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

     // Przypisz miejsce do pasażera
     passenger.setSeatNumber(seatNumber);
     passengerRepository.save(passenger);

     // Dodaj miejsce do listy zajętych miejsc
     flight.addSeat(seatNumber);
     flightRepository.save(flight);

     return "Miejsce " + seatNumber + " zostało przypisane do pasażera " + passenger.getName();
 }
}
