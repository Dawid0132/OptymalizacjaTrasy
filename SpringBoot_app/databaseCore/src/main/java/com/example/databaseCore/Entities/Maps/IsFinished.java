package com.example.databaseCore.Entities.Maps;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class IsFinished {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Boolean isFinished = Boolean.FALSE;

    private LocalDate finishedAt;

    @OneToOne
    @JoinColumn(name = "trip_id", unique = true)
    @JsonBackReference
    private Trips trips;

    public IsFinished(Long id, Boolean isFinished, LocalDate finishedAt, Trips trips) {
        this.id = id;
        this.isFinished = isFinished;
        this.finishedAt = finishedAt;
        this.trips = trips;
    }

    public IsFinished() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public LocalDate getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDate finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Trips getTrips() {
        return trips;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }
}
