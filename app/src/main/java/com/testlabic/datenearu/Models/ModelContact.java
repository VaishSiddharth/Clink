package com.testlabic.datenearu.Models;

public class ModelContact {
    
    String name;
    String image;
    String uid;
    String oneLine;
    Boolean blockStatus;
    
    public Boolean getBlockStatus() {
        return blockStatus;
    }
    
    public ModelContact() {
    }
    
    public String getOneLine() {
    
        return oneLine;
    }
    
    public ModelContact(String name, String image, String uid, String oneLine) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.oneLine = oneLine;
        
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
