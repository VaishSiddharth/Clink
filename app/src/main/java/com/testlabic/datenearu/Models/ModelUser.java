package com.testlabic.datenearu.Models;

public class ModelUser {
    
    String userName;
    String imageUrl;
    String age;
    LatLong location;
    String cityLocation;
    String about;
    String cityLabel;
    
    public ModelUser() {
    }
    
    public ModelUser(String userName, String imageUrl, String age, LatLong location, String cityLocation
    , String about) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.age = age;
        this.location = location;
        this.cityLocation = cityLocation;
        this.about = about;
    }
    
    public String getAbout() {
        return about;
    }
    
    public String getCityLabel() {
        return cityLabel;
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
