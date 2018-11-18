package com.testlabic.datenearu.Models;

import com.testlabic.datenearu.QuestionUtils.ModelQuestion;

import java.util.ArrayList;

public class ModelUser {
    
    String userName;
    String imageUrl;
    String age;
    LatLong location;
    String cityLocation;
    String about;
    String cityLabel;
    ArrayList<ModelQuestion> questions;
    String uid;
    
    public ModelUser() {
    }
    
    public String getUid() {
        return uid;
    }
    
    public ModelUser(String userName, String imageUrl, String age, LatLong location, String cityLocation
    , String about, String uid) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.age = age;
        this.location = location;
        this.cityLocation = cityLocation;
        this.about = about;
        this.uid = uid;
        
    }
    
    public ArrayList<ModelQuestion> getQuestions() {
        return questions;
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
