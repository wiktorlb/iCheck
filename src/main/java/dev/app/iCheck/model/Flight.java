package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 * Flight Entity Construct
 *
 */

@Document(collection = "flight")
public class Flight {

    @Id
    private String id;
    private String flightNumber;
    private String route;
    private String state;
    private String departureTime;
    private String departureDate;
    private Destination destination; // Powiązanie z obiektem Destination

    // Domyślny konstruktor
    public Flight() {
    }

    // Konstruktor z argumentami
    public Flight(String flightNumber, String route, String state, String departureTime) {
        this.flightNumber = flightNumber;
        this.route = route;
        this.state = state;
        this.departureTime = departureTime;
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
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
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

}