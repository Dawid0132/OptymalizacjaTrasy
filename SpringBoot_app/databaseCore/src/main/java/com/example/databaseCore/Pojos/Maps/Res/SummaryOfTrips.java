package com.example.databaseCore.Pojos.Maps.Res;

import java.time.LocalDate;

public class SummaryOfTrips {
    private LocalDate startDateOfTrip;
    private LocalDate endDateOfTrip;
    private LocalDate realisedEndDateOfTrip;
    private Long distance;
    private Long duration;
    private Long realisedDuration;

    public SummaryOfTrips(LocalDate startDateOfTrip, LocalDate endDateOfTrip, LocalDate realisedEndDateOfTrip, Long distance, Long duration, Long realisedDuration) {
        this.startDateOfTrip = startDateOfTrip;
        this.endDateOfTrip = endDateOfTrip;
        this.realisedEndDateOfTrip = realisedEndDateOfTrip;
        this.distance = distance;
        this.duration = duration;
        this.realisedDuration = realisedDuration;
    }

    public SummaryOfTrips() {
    }

    public LocalDate getStartDateOfTrip() {
        return startDateOfTrip;
    }

    public void setStartDateOfTrip(LocalDate startDateOfTrip) {
        this.startDateOfTrip = startDateOfTrip;
    }

    public LocalDate getEndDateOfTrip() {
        return endDateOfTrip;
    }

    public void setEndDateOfTrip(LocalDate endDateOfTrip) {
        this.endDateOfTrip = endDateOfTrip;
    }

    public LocalDate getRealisedEndDateOfTrip() {
        return realisedEndDateOfTrip;
    }

    public void setRealisedEndDateOfTrip(LocalDate realisedEndDateOfTrip) {
        this.realisedEndDateOfTrip = realisedEndDateOfTrip;
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

    public Long getRealisedDuration() {
        return realisedDuration;
    }

    public void setRealisedDuration(Long realisedDuration) {
        this.realisedDuration = realisedDuration;
    }
}
