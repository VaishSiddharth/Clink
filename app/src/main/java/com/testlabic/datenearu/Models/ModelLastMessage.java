package com.testlabic.datenearu.Models;

public class ModelLastMessage {
    
    private String name;
    private String imageUrl;
    private String uid;
    private String lastMessage;
    private long timeStamp;
    private Boolean isDelivered;
    private String sendersUid;
    private String status;
    private Boolean successfullySent;
    private Boolean temporaryContact;
    private String tempUid;
    public ModelLastMessage() {
    }
    
    public String getTempUid() {
        return tempUid;
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
    
    public Boolean getTemporaryContact() {
        return temporaryContact;
    }
    
    public ModelLastMessage(String name, String imageUrl, String uid, String lastMessage, long timeStamp,
                            Boolean isDelivered, String sendersUid, String status, Boolean successfullySent
    , Boolean temporaryContact, String tempUid) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.isDelivered = isDelivered;
        this.sendersUid = sendersUid;
        this.status = status;
        this.successfullySent= successfullySent;
        this.temporaryContact = temporaryContact;
        this.tempUid = tempUid;
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
    
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
