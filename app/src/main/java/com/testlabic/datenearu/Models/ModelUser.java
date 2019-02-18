package com.testlabic.datenearu.Models;

import java.util.ArrayList;

public class ModelUser {
    
    private String userName;
    private String userLastName;
    private String imageUrl;
    private String image2;
    private String image3;
    private String image1;
    
    public String getImage1() {
        return image1;
    }
    
    public String getImage2() {
        return image2;
    }
    
    public String getImage3() {
        return image3;
    }
    
    private String age;
    LatLong location;
    private String cityLocation;
    private String about;
    private String cityLabel;
    
    public void setQuestions(ArrayList<ModelQuestion> questions) {
        this.questions = questions;
    }
    
    private ArrayList<ModelQuestion> questions;
    private String uid;
    private int numeralAge;
    private String interestedIn;
    private String dateOfBirth;
    private String gender;
    private String oneLine;
    private String matchAlgo;
    private double matchIndex;
    private boolean isBlur;
    private boolean isQuestionaireComplete;
    
    public boolean isQuestionaireComplete() {
        return isQuestionaireComplete;
    }
    
    public boolean isBlur() {
        return isBlur;
    }
    
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
