package dev.app.iCheck.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "flight")
public class Flight {

    @Id
    private String id;

    private String flightNumber;
    private String route;
    private String status; // Zmieniono state na status
    private String departureTime;
    private String departureDate;
    private String aircraftId; // Referencja do konkretnego samolotu
    private List<String> occupiedSeats = new ArrayList<>(); // Zajęte miejsca
/*     private List<List<String>> seatMap = new ArrayList<>(); // Inicjalizacja */
private List<String> seatMap = new ArrayList<>();
    private String planeId;
    private Destination destination;
    private List<Passenger> passengers = new ArrayList<>();

    public Flight() {
    }

/*     public Flight(String id, String flightNumber, String route, String status, String departureDate,
            String departureTime, String aircraftId, String planeId, List<List<String>> seatMap,
            List<String> occupiedSeats, List<Passenger> passengers) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.route = route;
        this.status = status;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.aircraftId = aircraftId;
        this.planeId = planeId;
        this.seatMap = seatMap;
        this.occupiedSeats = occupiedSeats;
        this.passengers = passengers;
    }
 */
public Flight(String id, String flightNumber, String route, String status, String departureDate,
        String departureTime, String aircraftId, String planeId, List<String> seatMap,
        List<String> occupiedSeats, List<Passenger> passengers) {
    this.id = id;
    this.flightNumber = flightNumber;
    this.route = route;
    this.status = status;
    this.departureDate = departureDate;
    this.departureTime = departureTime;
    this.aircraftId = aircraftId;
    this.planeId = planeId;
    this.seatMap = seatMap;
    this.occupiedSeats = occupiedSeats;
    this.passengers = passengers;
}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(String aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public List<String> getOccupiedSeats() {
        return occupiedSeats;
    }

    public boolean isSeatOccupied(String seat) {
        return occupiedSeats.contains(seat);
    }

    public void addSeat(String seat) {
        occupiedSeats.add(seat);
    }

/*     public List<List<String>> getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(List<List<String>> seatMap) {
        this.seatMap = seatMap;
    } */
   public List<String> getSeatMap() {
    return seatMap;
}

public void setSeatMap(List<String> seatMap) {
    this.seatMap = seatMap;
}

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", route='" + route + '\'' +
                ", status='" + status + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", aircraftId='" + aircraftId + '\'' +
                ", planeId='" + planeId + '\'' +
                ", seatMap=" + seatMap +
                '}';
    }

    // Enum Status
    public enum FlightStatus {
        PREPARE,
        OPEN,
        CLOSED,
        FINALIZED;
    }

}
