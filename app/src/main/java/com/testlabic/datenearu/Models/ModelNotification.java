package com.testlabic.datenearu.Models;

import java.util.HashMap;

public class ModelNotification {
    String message;
    String sendersUid;
    long timeStamp;
    String imageUrl;
    
    public ModelNotification(String message, String sendersUid, long timeStamp, String imageUrl) {
        this.message = message;
        this.sendersUid = sendersUid;
        this.timeStamp = timeStamp;
        this.imageUrl =imageUrl;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public ModelNotification() {
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getSendersUid() {
        return sendersUid;
    }
    
    public long getTimeStamp() {
        return timeStamp;
    }
}
