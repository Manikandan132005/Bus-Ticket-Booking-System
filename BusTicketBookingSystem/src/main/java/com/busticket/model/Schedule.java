package com.busticket.model;

import java.time.LocalDateTime;

public class Schedule {
    private int busId;
    private int routeId;
    private int scheduleId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int availableSeats;
    private String status;          // SCHEDULED / CANCELLED / COMPLETED

    // Joined fields for display
    private String busName;
    private String busNumber;
    private String busType;
    private String source;
    private String destination;
    private double farePerSeat;

    public Schedule() {}

    // ---- Core fields ----
    public int   getScheduleId()     { return scheduleId; }
    public void  setScheduleId(int id){ this.scheduleId = id; }

    public int   getBusId()          { return busId; }
    public void  setBusId(int id)    { this.busId = id; }

    public int   getRouteId()        { return routeId; }
    public void  setRouteId(int id)  { this.routeId = id; }

    public LocalDateTime getDepartureTime()           { return departureTime; }
    public void          setDepartureTime(LocalDateTime t){ this.departureTime = t; }

    public LocalDateTime getArrivalTime()             { return arrivalTime; }
    public void          setArrivalTime(LocalDateTime t){ this.arrivalTime = t; }

    public int   getAvailableSeats()       { return availableSeats; }
    public void  setAvailableSeats(int s)  { this.availableSeats = s; }

    public String getStatus()              { return status; }
    public void   setStatus(String s)      { this.status = s; }

    // ---- Joined fields ----
    public String getBusName()      { return busName; }
    public void   setBusName(String n){ this.busName = n; }

    public String getBusNumber()    { return busNumber; }
    public void   setBusNumber(String n){ this.busNumber = n; }

    public String getBusType()      { return busType; }
    public void   setBusType(String t){ this.busType = t; }

    public String getSource()       { return source; }
    public void   setSource(String s){ this.source = s; }

    public String getDestination()       { return destination; }
    public void   setDestination(String d){ this.destination = d; }

    public double getFarePerSeat()       { return farePerSeat; }
    public void   setFarePerSeat(double f){ this.farePerSeat = f; }

    @Override
    public String toString() {
        return String.format(
            "Sched #%-3d | %-18s | %s → %-14s | Dep: %s | Avail: %2d | Type: %-12s | ₹%.2f/seat",
            scheduleId, busName, source, destination, departureTime, availableSeats, busType, farePerSeat);
    }
}
