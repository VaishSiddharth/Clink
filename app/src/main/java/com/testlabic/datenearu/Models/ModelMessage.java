package com.testlabic.datenearu.Models;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class ModelMessage {
    
    String  imageUrl;
    String  sendersName;
    long time;
    String text;
    
    public ModelMessage() {
    }
    
    public ModelMessage(String imageUrl, String sendersName, long time, String text) {
        this.imageUrl = imageUrl;
        this.sendersName = sendersName;
        this.time = time;
        this.text = text;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getSendersName() {
        return sendersName;
    }
    
    public long getTime() {
        return time;
    }
    
    public String getText() {
        return text;
    }
}
