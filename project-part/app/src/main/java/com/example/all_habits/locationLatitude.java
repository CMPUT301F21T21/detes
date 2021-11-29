package com.example.all_habits;

/**
 * Gets the latitude of location
 */
public class locationLatitude {

    //initialize
    private double latitude;

    public locationLatitude (double latitude) {
        this.latitude = latitude;
    }

    //gets latitude
    public double getLatitude() { return latitude; }

    //sets latitude
    public void setLatitude(double latitude) { this.latitude = latitude; }
}
