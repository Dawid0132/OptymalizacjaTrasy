package com.example.databaseCore.Entities.Maps;

import com.example.databaseCore.Entities.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Entity
@NamedQuery(name = "Coordinates.findByUserId", query = "select c from Coordinates c where c.user.id=?1")
public class Coordinates {

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
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Coordinates(Long id, Float latitude, Float longitude, User user) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public Coordinates() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
