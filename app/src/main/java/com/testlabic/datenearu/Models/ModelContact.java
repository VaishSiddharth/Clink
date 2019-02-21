package com.testlabic.datenearu.Models;

import java.util.HashMap;

public class ModelContact {
    
    String name;
    String image;
    String uid;
    String oneLine;
    Boolean blockStatus;
    HashMap<String, Object> timeStamp;
    Boolean directMessage;
    
    public HashMap<String, Object> getTimeStamp() {
        return timeStamp;
    }
    
    public Boolean getBlockStatus() {
        return blockStatus;
    }
    
    public ModelContact() {
    }
    
    public String getOneLine() {
    
        return oneLine;
    }
    
    public Boolean getDirectMessage() {
        return directMessage;
    }
    
    public ModelContact(String name, String image, String uid, String oneLine, HashMap<String, Object> timeStamp) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.oneLine = oneLine;
        this.timeStamp = timeStamp;
    }
    
    public ModelContact(String name, String image, String uid, String oneLine, HashMap<String, Object> timeStamp, Boolean directMessage) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.oneLine = oneLine;
        this.timeStamp = timeStamp;
        this.directMessage = directMessage;
    }
    
    public String getName() {
        return name;
    }
    
    public String getImage() {
        return image;
    }
    
    public String getUid() {
        return uid;
    }
}
