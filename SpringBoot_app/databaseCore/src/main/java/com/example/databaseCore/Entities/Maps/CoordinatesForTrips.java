package com.example.databaseCore.Entities.Maps;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class CoordinatesForTrips {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private Float latitude;

    @Column
    @NotNull
    private Float longitude;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonBackReference
    private Trips trips;

    public CoordinatesForTrips(Long id, Float latitude, Float longitude, Trips trips) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.trips = trips;
    }

    public CoordinatesForTrips() {
    }

    public Trips getTrips() {
        return trips;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
