package dev.app.iCheck.controller;

public class SeatAssignmentRequest {
    private String flightId;
    private String passengerId;
    private String seatNumber;

    // 🔹 Konstruktor bezargumentowy (wymagany przez Jackson do deserializacji
    // JSON-a)
    public SeatAssignmentRequest() {
    }

    public SeatAssignmentRequest(String flightId, String passengerId, String seatNumber) {
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
    }

    // 🔹 Gettery i settery – wymagane do mapowania JSON -> Java
    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
