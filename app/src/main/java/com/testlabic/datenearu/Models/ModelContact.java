package com.testlabic.datenearu.Models;

public class ModelContact {
    
    String name;
    String image;
    String uid;
    
    public ModelContact() {
    }
    
    public ModelContact(String name, String image, String uid) {
        this.name = name;
        this.image = image;
        this.uid = uid;
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
