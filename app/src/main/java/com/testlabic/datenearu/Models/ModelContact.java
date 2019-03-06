package com.testlabic.datenearu.Models;

import java.util.HashMap;

public class ModelContact {
    
    private String name;
    private String image;
    private String uid;
    private Boolean temporaryContact;
    private String tempUid;
    private boolean blockStatus;
    
    public boolean getBlockStatus() {
        return blockStatus;
    }
    
    public Boolean getTemporaryContact() {
        return temporaryContact;
    }
    
    public String getTempUid() {
        return tempUid;
    }
    
    public ModelContact() {
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
