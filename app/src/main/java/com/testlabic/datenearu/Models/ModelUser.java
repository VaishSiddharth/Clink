package com.testlabic.datenearu.Models;

import com.testlabic.datenearu.QuestionUtils.ModelQuestion;

import java.util.ArrayList;

public class ModelUser {
    
    String userName;
    String userLastName;
    String imageUrl;
    String age;
    LatLong location;
    String cityLocation;
    String about;
    String cityLabel;
    ArrayList<ModelQuestion> questions;
    String uid;
    int numeralAge;
    String interestedIn;
    String dateOfBirth;
    String gender;
    String oneLine;
    String matchAlgo;
    double matchIndex;
    boolean isBlur;
    
    public boolean getIsBlur() {
        return isBlur;
    }
    
    public double getMatchIndex() {
        return matchIndex;
    }
    
    public void setMatchIndex(double matchIndex) {
        this.matchIndex = matchIndex;
    }
    
    public String getMatchAlgo() {
        return matchAlgo;
    }
    
    public String getOneLine() {
        return oneLine;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public String getInterestedIn() {
        return interestedIn;
    }
    
    public ModelUser() {
    }
    
    public String getUid() {
        return uid;
    }
    
    public String getUserLastName() {
        return userLastName;
    }
    
    public ModelUser(String userName, String imageUrl, String age, LatLong location, String cityLocation
    , String about, String uid, String userLastName, int numeralAge) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.age = age;
        this.location = location;
        this.cityLocation = cityLocation;
        this.about = about;
        this.uid = uid;
        this.userLastName = userLastName;
        this.numeralAge = numeralAge;
    }
    
    public int getNumeralAge() {
        return numeralAge;
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
