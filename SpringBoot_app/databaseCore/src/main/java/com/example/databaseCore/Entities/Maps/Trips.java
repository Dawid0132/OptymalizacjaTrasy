package com.example.databaseCore.Entities.Maps;

import com.example.databaseCore.Entities.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Trips {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "trips", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @NotNull
    private List<CoordinatesForTrips> coordinates = new ArrayList<>();

    @Column
    @NotNull
    private LocalDate startDate;

    @Column
    @NotNull
    private LocalDate endDate;

    @Column
    @NotNull
    private LocalDate createdAt;

    @Column
    @NotNull
    private Float duration;

    @Column
    @NotNull
    private Float distance;

    @Column
    @NotNull
    private Boolean finished;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Trips(Long id, List<CoordinatesForTrips> coordinates, LocalDate startDate, LocalDate endDate, LocalDate createdAt, Float duration, Float distance, Boolean finished, User user) {
        this.id = id;
        this.coordinates = coordinates;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.duration = duration;
        this.distance = distance;
        this.finished = finished;
        this.user = user;
    }

    public Trips() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CoordinatesForTrips> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinatesForTrips> coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
