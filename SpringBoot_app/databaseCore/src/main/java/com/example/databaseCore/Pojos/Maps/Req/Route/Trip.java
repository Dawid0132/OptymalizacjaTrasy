package com.example.databaseCore.Pojos.Maps.Req.Route;

import java.util.List;

public class Trip {
    private List<Object> legs;

    public Trip(List<Object> legs) {
        this.legs = legs;
    }

    public Trip() {
    }

    public List<Object> getLegs() {
        return legs;
    }

    public void setLegs(List<Object> legs) {
        this.legs = legs;
    }
}
