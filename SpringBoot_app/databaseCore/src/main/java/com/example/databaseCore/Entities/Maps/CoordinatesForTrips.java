package com.example.databaseCore.Entities.Maps;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class CoordinatesForTrips {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Min(-90)
    @Max(90)
    @NotNull
    private Float latitude;

    @Column
    @Min(-90)
    @Max(90)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Trips getTrips() {
        return trips;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }
}
