package com.example.databaseCore.Pojos.Maps.Req;

public class Coordinates_Req {
    private float latitude;
    private float longitude;

    public Coordinates_Req(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates_Req() {
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
