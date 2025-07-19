package com.example.databaseCore.Entities.Maps;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class VerifyClickedCoordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Min(-90)
    @Max(90)
    @NotNull
    private Float latitude;

    @Column
    @Min(-180)
    @Max(180)
    @NotNull
    private Float longitude;

    public VerifyClickedCoordinates(Long id, Float latitude, Float longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public VerifyClickedCoordinates() {
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
}
