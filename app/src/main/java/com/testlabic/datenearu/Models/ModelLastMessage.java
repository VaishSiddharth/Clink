package com.testlabic.datenearu.Models;

public class ModelLastMessage {
    
    String name;
    String imageUrl;
    String uid;
    String lastMessage;
    long timeStamp;
    
    public ModelLastMessage() {
    }
    
    public ModelLastMessage(String name, String imageUrl, String uid, String lastMessage, long timeStamp) {
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
    
    public long getTimeStamp() {
        return timeStamp;
    }
}
