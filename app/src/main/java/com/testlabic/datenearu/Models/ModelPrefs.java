package com.testlabic.datenearu.Models;

public class ModelPrefs {
    
    private double distanceLimit;
    private int minAge;
    private int maxAge;
    private String preferedGender;
    private int numberOfAns;
    
    public int getNumberOfAns() {
        return numberOfAns;
    }
    
    public String getMatchAlgo() {
        return matchAlgo;
    }
    
    private String matchAlgo;
    
    public ModelPrefs(double distanceLimit, int minAge, int maxAge, String preferedGender, int numberOfAns) {
        this.distanceLimit = distanceLimit;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.preferedGender = preferedGender;
        this.numberOfAns = numberOfAns;
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
