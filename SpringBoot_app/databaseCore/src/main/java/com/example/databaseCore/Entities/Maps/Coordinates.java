package com.example.mapauthrest.DB.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Entity
@NamedQuery(name = "Coordinates.findById", query = "select c from Coordinates c where c.id=?1")
@NamedQuery(name = "Coordinates.findAllByUserId", query = "select c from Coordinates c where c.userId=?1")
@NamedQuery(name = "Coordinates.deleteCoordinatesByIds", query = "delete from Coordinates c where c.id in ?1")
public class Coordinates {
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

    @Column
    @NotNull
    private Long userId;

    public Coordinates(Long id, Float latitude, Float longitude, Long userId) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public Coordinates() {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
