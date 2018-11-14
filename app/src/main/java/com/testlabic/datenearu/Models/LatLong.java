package com.testlabic.datenearu.Models;

public class LatLong {
    
    Double longitude;
    Double latitude;
    
    public LatLong(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public LatLong() {
    }
    
    public Double getLongitude() {
    
        return longitude;
    }
}
