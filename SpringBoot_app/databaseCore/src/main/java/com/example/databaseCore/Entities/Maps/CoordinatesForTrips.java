package com.example.databaseCore.Entities.Maps;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CoordinatesForTrips {

    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
