package com.example.databaseCore.Pojos.Maps.Req.SavedTrips;

import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;

import java.util.List;

public class TripReq {
    private List<Coordinates_Req> coordinates;

    private Long distance;

    private Long duration;

    public TripReq(List<Coordinates_Req> coordinates, Long distance, Long duration) {
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

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
