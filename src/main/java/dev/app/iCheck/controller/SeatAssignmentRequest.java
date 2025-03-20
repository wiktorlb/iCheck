package dev.app.iCheck.controller;

public class SeatAssignmentRequest {
    private String flightId;
    private String passengerId;
    private String seatNumber;

    public String getFlightId() {
        return flightId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
}
