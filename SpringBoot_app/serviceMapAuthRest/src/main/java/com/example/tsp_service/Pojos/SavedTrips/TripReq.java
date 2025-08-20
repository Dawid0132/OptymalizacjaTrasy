package com.example.tsp_service.Pojos.SavedTrips;

import com.example.tsp_service.Pojos.Coordinates;

import java.util.List;

public class TripReq {
    private List<Coordinates> coordinates;

    private Float distance;

    private Float duration;

    public TripReq(List<Coordinates> coordinates, Float distance, Float duration) {
        this.coordinates = coordinates;
        this.distance = distance;
        this.duration = duration;
    }

    public TripReq() {
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }
}
