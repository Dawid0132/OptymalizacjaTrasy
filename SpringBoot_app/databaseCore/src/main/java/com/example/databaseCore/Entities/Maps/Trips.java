package com.example.databaseCore.Entities.Maps;


import com.example.databaseCore.Entities.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NamedQuery(name = "Trips.findByNameAndUserId", query = "select t from Trips t where t.mapName=?1 and t.user.id=?2")
@NamedQuery(name = "Trips.findByIdAndUserId", query = "select t from Trips t where t.id=?1 and t.user.id=?2")
@NamedQuery(name = "Trips.getAllByFinishedCondition", query = "select t from Trips t where t.user.id=?1 and t.isFinished.isFinished=?2")
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
    private Long duration;

    @Column
    @NotNull
    private Long distance;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private IsFinished isFinished;

    @Column
    @NotNull
    private final UUID mapName = UUID.randomUUID();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private MeasuringTime measuringTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Trips(Long id, List<CoordinatesForTrips> coordinates, LocalDate startDate, LocalDate endDate, LocalDate createdAt, Long duration, Long distance, IsFinished isFinished, MeasuringTime measuringTime, User user) {
        this.id = id;
        this.coordinates = coordinates;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.duration = duration;
        this.distance = distance;
        this.isFinished = isFinished;
        this.measuringTime = measuringTime;
        this.user = user;
    }

    public Trips() {
    }

    public List<CoordinatesForTrips> addCoordinatesForTrips(CoordinatesForTrips coordinatesForTrips) {
        coordinatesForTrips.setTrips(this);
        coordinates.add(coordinatesForTrips);
        return coordinates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MeasuringTime getMeasuringTime() {
        return measuringTime;
    }

    public void setMeasuringTime(MeasuringTime measuringTime) {
        this.measuringTime = measuringTime;
    }

    public UUID getMapName() {
        return mapName;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public IsFinished getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(IsFinished isFinished) {
        this.isFinished = isFinished;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
