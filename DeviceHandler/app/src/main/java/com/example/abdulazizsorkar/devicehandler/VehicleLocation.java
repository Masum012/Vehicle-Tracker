package com.example.abdulazizsorkar.devicehandler;

/**
 * Created by Abdul Aziz Sorkar on 1/18/2016.
 */
public class VehicleLocation {
    double latitide,longitude;
    String dateTime=null;
    public VehicleLocation(double latitide, double longitude,String dateTime) {
        this.latitide = latitide;
        this.longitude = longitude;
        this.dateTime = dateTime;
    }
}
