package com.testlabic.datenearu.Models;

public class ModelDisplay {
    
    String name;
    String url;
    String age;
    String place;
    
    public ModelDisplay(String name, String url, String age, String place) {
        this.name = name;
        this.url = url;
        this.age = age;
        this.place = place;
    }
    
    public ModelDisplay() {
    }
    
    public String getName() {
        return name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getAge() {
        return age;
    }
    
    public String getPlace() {
        return place;
    }
}
