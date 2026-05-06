package com.busticket.model;

public class Route {
    private int routeId;
    private String source;
    private String destination;
    private int distanceKm;

    public Route() {}

    public int    getRouteId()      { return routeId; }
    public void   setRouteId(int id){ this.routeId = id; }

    public String getSource()        { return source; }
    public void   setSource(String s){ this.source = s; }

    public String getDestination()        { return destination; }
    public void   setDestination(String d){ this.destination = d; }

    public int    getDistanceKm()         { return distanceKm; }
    public void   setDistanceKm(int d)    { this.distanceKm = d; }

    @Override
    public String toString() {
        return String.format("Route #%d | %s → %s (%d km)",
                routeId, source, destination, distanceKm);
    }
}
