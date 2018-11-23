package com.testlabic.datenearu.Models;

import java.util.HashMap;

public class ModelLastMessage {
    
    String name;
    String imageUrl;
    String uid;
    String lastMessage;
    HashMap<String, Object> timeStamp;
    
    public ModelLastMessage() {
    }
    
    public ModelLastMessage(String name, String imageUrl, String uid, String lastMessage, HashMap<String, Object> timeStamp) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }
    
    public String getName() {
        return name;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getUid() {
        return uid;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public HashMap<String, Object> getTimeStamp() {
        return timeStamp;
    }
}
