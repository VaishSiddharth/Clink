package com.testlabic.datenearu.Models;

public class ModelCityLabel
{
    String cityLabel;
    int number;
    
    public ModelCityLabel(String cityLabel, int number) {
        this.cityLabel = cityLabel;
        this.number = number;
    }
    
    public String getCityLabel() {
        return cityLabel;
    }
    
    public int getNumber() {
        return number;
    }
}
