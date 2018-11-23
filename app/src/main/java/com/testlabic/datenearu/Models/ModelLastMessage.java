package com.testlabic.datenearu.Models;

public class ModelLastMessage {
    
    String name;
    String imageUrl;
    String uid;
    String lastMessage;
    long timeStamp;
    Boolean isDelivered;
    String sendersUid;
    
    public ModelLastMessage() {
    }
    
    public Boolean getDelivered() {
        return isDelivered;
    }
    
    public ModelLastMessage(String name, String imageUrl, String uid, String lastMessage, long timeStamp,
                            Boolean isDelivered, String sendersUid) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.isDelivered = isDelivered;
        this.sendersUid = sendersUid;
        
    }
    
    public String getName() {
        return name;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getSendersUid() {
        return sendersUid;
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
