package com.testlabic.datenearu.Models;

import java.util.HashMap;

public class ModelContact {
    
    private String name;
    private String image;
    private String uid;
    private String oneLine;
    private Boolean blockStatus;
    private HashMap<String, Object> timeStamp;
    private Boolean temporaryContact;
    private String tempUid;
    
    public HashMap<String, Object> getTimeStamp() {
        return timeStamp;
    }
    
    public Boolean getBlockStatus() {
        return blockStatus;
    }
    
    public ModelContact() {
    }
    
   
    
    public ModelContact(String name, String image, String uid, String oneLine, HashMap<String, Object> timeStamp) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.oneLine = oneLine;
        this.timeStamp = timeStamp;
    }
    
    public ModelContact(String name, String image, String uid,
                        Boolean temporaryContact
    , String tempUid) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.temporaryContact = temporaryContact;
        this.tempUid = tempUid;
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
