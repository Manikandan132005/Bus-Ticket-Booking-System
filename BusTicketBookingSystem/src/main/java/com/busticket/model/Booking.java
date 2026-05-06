package com.busticket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int userId;
    private int scheduleId;
    private int seatsBooked;
    private BigDecimal totalFare;
    private String bookingStatus;      // CONFIRMED / CANCELLED / PENDING
    private LocalDateTime bookedAt;

    // Joined display fields
    private String userName;
    private String source;
    private String destination;
    private String busName;
    private LocalDateTime departureTime;

    public Booking() {}

    // ---- Core ----
    public int   getBookingId()       { return bookingId; }
    public void  setBookingId(int id) { this.bookingId = id; }

    public int   getUserId()          { return userId; }
    public void  setUserId(int id)    { this.userId = id; }

    public int   getScheduleId()      { return scheduleId; }
    public void  setScheduleId(int id){ this.scheduleId = id; }

    public int   getSeatsBooked() { return seatsBooked; }
    public void  setSeatsBooked(int s)   { this.seatsBooked = s; }

    public BigDecimal getTotalFare() { return totalFare; }
    public void       setTotalFare(BigDecimal f){ this.totalFare = f; }

    public String getBookingStatus()      { return bookingStatus; }
    public void   setBookingStatus(String s){ this.bookingStatus = s; }

    public LocalDateTime getBookedAt()    { return bookedAt; }
    public void          setBookedAt(LocalDateTime t){ this.bookedAt = t; }

    // ---- Joined ----
    public String getUserName()    { return userName; }
    public void   setUserName(String n){ this.userName = n; }

    public String getSource()      { return source; }
    public void   setSource(String s){ this.source = s; }

    public String getDestination() { return destination; }
    public void   setDestination(String d){ this.destination = d; }

    public String getBusName()     { return busName; }
    public void   setBusName(String n){ this.busName = n; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime t){ this.departureTime = t; }

    @Override
    public String toString() {
        return String.format(
            "Booking #%-4d | %-18s | %s → %-14s | Seats: %d | Fare: ₹%-8.2f | Status: %-10s | %s",
            bookingId, busName, source, destination,
            seatsBooked, totalFare, bookingStatus, bookedAt);
    }
}
