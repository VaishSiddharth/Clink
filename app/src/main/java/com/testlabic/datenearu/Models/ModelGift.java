package com.testlabic.datenearu.Models;

import java.io.Serializable;
import java.util.HashMap;

public class ModelGift  implements Serializable {
    
    private String giftSendersUid;
    private String giftType;
    private String giftReceiversUid;
    private String giftSendersName;
    private String giftSendersImageUrl;
    private long timeStamp;
    
    public ModelGift()
    {}
    
    public String getGiftSendersUid() {
        return giftSendersUid;
    }
    
    public String getGiftType() {
        return giftType;
    }
    
    public String getGiftReceiversUid() {
        return giftReceiversUid;
    }
    
    public String getGiftSendersName() {
        return giftSendersName;
    }
    
    public String getGiftSendersImageUrl() {
        return giftSendersImageUrl;
    }
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public ModelGift(String giftSendersUid, String giftType, String giftReceiversUid, String giftSendersName, String giftSendersImageUrl, long timeStamp) {
        
        this.giftSendersUid = giftSendersUid;
        this.giftType = giftType;
        this.giftReceiversUid = giftReceiversUid;
        this.giftSendersName = giftSendersName;
        this.giftSendersImageUrl = giftSendersImageUrl;
        this.timeStamp = timeStamp;
    }
}
