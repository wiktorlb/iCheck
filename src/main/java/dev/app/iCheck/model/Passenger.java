package dev.app.iCheck.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "passenger")
@CompoundIndex(def = "{'flightId': 1}")
public class Passenger {
    @Id
    private String id;
    private String flightId;
    private String name;
    private String surname;
    private String gender;
    private String status;
    private String title;

    private List<Baggage> baggageList = new ArrayList<>(); // 🔹 Inicjalizacja listy; // Lista bagaży dla pasażera
    // W modelu pasażera
@Field("comments")
private List<Comment> comments = new ArrayList<>();


private String seatNumber; // Numer miejsca pasażera




public static class Comment {
    private String text;
    private String date;
    private String addedBy; // Możesz dodać również użytkownika, który dodał komentarz
    // Gettery i settery
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getAddedBy() {
        return addedBy;
    }
    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

}

    // Konstruktor, gettery i settery
    public Passenger(String id, String flightId, String name, String surname, String gender, String status, String title, String seatNumber) {
        this.id = id;
        this.flightId = flightId;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.status = status;
        this.title = title;

        this.baggageList = new ArrayList<>(); // 🔹 Upewniamy się, że lista jest inicjalizowana
        this.comments = new ArrayList<>();

        this.seatNumber = seatNumber; // 🔹 Inicjalizacja pola
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public List<Baggage> getBaggageList() {
        if (baggageList == null) { // 🔹 Dodane zabezpieczenie
            baggageList = new ArrayList<>();
        }
        return baggageList;
    }
    public void setBaggageList(List<Baggage> baggageList) {
        this.baggageList = baggageList;
    }

    public List<Comment> getComments() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }



    public List<String> getSRRCodes() {
        List<String> codes = new ArrayList<>();

        // Sprawdź czy pasażer ma dane API
        if (this instanceof PassengerAPI) {
            codes.add("DOCS");
        }

        // Sprawdź bagaże
        if (baggageList != null && !baggageList.isEmpty()) {
            for (int i = 0; i < baggageList.size(); i++) {
                codes.add("BAG" + (i + 1));
            }
        }

        // Sprawdź komentarze
        if (comments != null && !comments.isEmpty()) {
            codes.add("COM");
        }

        return codes;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber, Plane plane) {
        if (!plane.isSeatAvailable(seatNumber)) {
            throw new IllegalArgumentException("Seat " + seatNumber + " is already occupied!");
        }
        this.seatNumber = seatNumber;
        plane.assignSeat(seatNumber);
    }
}