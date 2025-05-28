package dev.app.iCheck.controller;

/**
 * Represents a request for assigning or releasing a seat for a passenger on a flight.
 */
public class SeatAssignmentRequest {
    private String flightId;
    private String passengerId;
    private String seatNumber;

    /**
     * Default constructor (required by Jackson for JSON deserialization).
     */
    public SeatAssignmentRequest() {
    }

    /**
     * Constructs a new SeatAssignmentRequest with the specified flight ID, passenger ID, and seat number.
     *
     * @param flightId    The ID of the flight.
     * @param passengerId The ID of the passenger.
     * @param seatNumber  The seat number.
     */
    public SeatAssignmentRequest(String flightId, String passengerId, String seatNumber) {
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
    }

    /**
     * Gets the flight ID.
     *
     * @return The flight ID.
     */
    public String getFlightId() {
        return flightId;
    }

    /**
     * Sets the flight ID.
     *
     * @param flightId The flight ID to set.
     */
    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    /**
     * Gets the passenger ID.
     *
     * @return The passenger ID.
     */
    public String getPassengerId() {
        return passengerId;
    }

    /**
     * Sets the passenger ID.
     *
     * @param passengerId The passenger ID to set.
     */
    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    /**
     * Gets the seat number.
     *
     * @return The seat number.
     */
    public String getSeatNumber() {
        return seatNumber;
    }

    /**
     * Sets the seat number.
     *
     * @param seatNumber The seat number to set.
     */
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
