package com.testlabic.datenearu.Models;

public class ModelLastMessage {
    
    String name;
    String imageUrl;
    String uid;
    String lastMessage;
    long timeStamp;
    Boolean isDelivered;
    String sendersUid;
    String status;
    Boolean successfullySent;
    public ModelLastMessage() {
    }

    public Boolean getSuccessfullySent() {
        return successfullySent;
    }

    public Boolean getDelivered() {
        return isDelivered;
    }
    
    public String getStatus() {
        return status;
    }
    
    public ModelLastMessage(String name, String imageUrl, String uid, String lastMessage, long timeStamp,
                            Boolean isDelivered, String sendersUid, String status, Boolean successfullySent) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.isDelivered = isDelivered;
        this.sendersUid = sendersUid;
        this.status = status;
        this.successfullySent= successfullySent;
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
