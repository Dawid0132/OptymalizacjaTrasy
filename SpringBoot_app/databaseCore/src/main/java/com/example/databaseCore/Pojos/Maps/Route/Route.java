package com.example.mapauthrest.Pojo.Route;

import java.util.List;

public class Route {
    private List<Trip> trips;

    public Route(List<Trip> trips) {
        this.trips = trips;
    }

    public Route() {
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
