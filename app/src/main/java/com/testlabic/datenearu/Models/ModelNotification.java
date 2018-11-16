package com.testlabic.datenearu.Models;

import java.util.HashMap;

public class ModelNotification {
    String message;
    String sendersUid;
    long timeStamp;
    
    public ModelNotification(String message, String sendersUid, long timeStamp) {
        this.message = message;
        this.sendersUid = sendersUid;
        this.timeStamp = timeStamp;
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
