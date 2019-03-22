package com.testlabic.datenearu.Models;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;

public class ModelSubscr {
    @PropertyName("XPoints")
    private int XPoints;
   private long lastUpdateTime;
   
   @PropertyName("XPoints")
    public int getXPoints() {
        return XPoints;
    }
    
    public ModelSubscr() {
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
