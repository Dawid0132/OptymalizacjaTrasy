package com.example.databaseCore.Pojos.Maps.Req.SavedTrips;

import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;

import java.util.List;

public class TripReq {
    private List<Coordinates_Req> coordinates;

    private Float distance;

    private Float duration;

    public TripReq(List<Coordinates_Req> coordinates, Float distance, Float duration) {
        this.coordinates = coordinates;
        this.distance = distance;
        this.duration = duration;
    }

    public TripReq() {
    }

    public List<Coordinates_Req> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates_Req> coordinates) {
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
