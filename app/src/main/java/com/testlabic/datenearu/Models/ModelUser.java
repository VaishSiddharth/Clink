package com.testlabic.datenearu.Models;

public class ModelUser {
    
    String userName;
    String imageUrl;
    String age;
    LatLong location;
    String cityLocation;
    
    public ModelUser(String userName, String imageUrl, String age, LatLong location, String cityLocation) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.age = age;
        this.location = location;
        this.cityLocation = cityLocation;
    }
    
    public String getCityLocation() {
        return cityLocation;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getAge() {
        return age;
    }
    
    public LatLong getLocation() {
        return location;
    }
}
