package com.busticket.model;

import java.math.BigDecimal;

public class Bus {
    private int busId;
    private String busNumber;
    private String busName;
    private int totalSeats;
    private String busType;        // AC / NON_AC / SLEEPER / SEMI_SLEEPER
    private BigDecimal farePerSeat;

    public Bus() {}

    // Getters & Setters
    public int        getBusId()       { return busId; }
    public void       setBusId(int id) { this.busId = id; }

    public String     getBusNumber()   { return busNumber; }
    public void       setBusNumber(String n) { this.busNumber = n; }

    public String     getBusName()     { return busName; }
    public void       setBusName(String n) { this.busName = n; }

    public int        getTotalSeats()  { return totalSeats; }
    public void       setTotalSeats(int s) { this.totalSeats = s; }

    public String     getBusType()     { return busType; }
    public void       setBusType(String t) { this.busType = t; }

    public BigDecimal getFarePerSeat() { return farePerSeat; }
    public void       setFarePerSeat(BigDecimal f) { this.farePerSeat = f; }

    @Override
    public String toString() {
        return String.format("Bus #%d | %-12s | %-20s | Type: %-12s | Seats: %2d | Fare: ₹%.2f",
                busId, busNumber, busName, busType, totalSeats, farePerSeat);
    }
}
