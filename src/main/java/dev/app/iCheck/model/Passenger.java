package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "passenger")
public class Passenger {
    @Id
    private String id;
    private String flightId;
    private String name;
    private String surname;
    private String gender;

    // Konstruktor, gettery i settery
    public Passenger(String id, String flightId, String name, String surname, String gender) {
        this.id = id;
        this.flightId = flightId;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFlightId() {
        return flightId;
    }
    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
}