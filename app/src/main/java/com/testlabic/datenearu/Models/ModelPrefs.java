package com.testlabic.datenearu.Models;

public class ModelPrefs {
    
    private double distanceLimit;
    private int minAge;
    private int maxAge;
    String preferedGender;
    
    public ModelPrefs(double distanceLimit, int minAge, int maxAge, String preferedGender) {
        this.distanceLimit = distanceLimit;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.preferedGender = preferedGender;
    }
    
    public ModelPrefs() {
    
    }
    
    public double getDistanceLimit() {
        return distanceLimit;
    }
    
    public int getMinAge() {
        return minAge;
    }
    
    public int getMaxAge() {
        return maxAge;
    }
    
    public String getPreferedGender() {
        return preferedGender;
    }
}
