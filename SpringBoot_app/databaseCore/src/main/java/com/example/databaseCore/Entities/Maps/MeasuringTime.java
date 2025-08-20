package com.example.databaseCore.Entities.Maps;

import com.example.databaseCore.Entities.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class MeasuringTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Boolean isDriving = Boolean.FALSE;

    @NotNull
    private LocalDateTime changingStatus = LocalDateTime.now();

    @NotNull
    private Long total = 0L;

    @OneToOne
    @JoinColumn(name = "trip_id", unique = true)
    @JsonBackReference
    private Trips trips;


    public MeasuringTime(Long id, Boolean isDriving, LocalDateTime changingStatus, Long total, Trips trips) {
        this.id = id;
        this.isDriving = isDriving;
        this.changingStatus = changingStatus;
        this.total = total;
        this.trips = trips;
    }

    public MeasuringTime() {
    }

    public Trips getTrips() {
        return trips;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }

    public Boolean getDriving() {
        return isDriving;
    }

    public void setDriving(Boolean driving) {
        isDriving = driving;
    }

    public LocalDateTime getChangingStatus() {
        return changingStatus;
    }

    public void setChangingStatus(LocalDateTime changingStatus) {
        this.changingStatus = changingStatus;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
