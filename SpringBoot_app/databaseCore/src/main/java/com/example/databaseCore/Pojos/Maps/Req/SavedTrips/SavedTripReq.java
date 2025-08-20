package com.example.databaseCore.Pojos.Maps.Req.SavedTrips;

import java.time.LocalDate;

public class SavedTripReq {
    private TripReq trip;

    private LocalDate startDate;

    public SavedTripReq(TripReq trip, LocalDate startDate) {
        this.trip = trip;
        this.startDate = startDate;
    }

    public SavedTripReq() {
    }

    public TripReq getTrip() {
        return trip;
    }

    public void setTrip(TripReq trip) {
        this.trip = trip;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
