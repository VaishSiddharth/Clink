package com.testlabic.datenearu.Models;

public class ModelPurchase {
    
    private String skuId;
    private String originalJson;
    private String orderId;
    private String purchaseTime;
    private String purchaseToken;
    private String originalSignature;
    private int numberOfDrops;
    
    public ModelPurchase() {
    }
    
    public ModelPurchase(String skuId, String originalJson, String orderId, String purchaseTime, String purchaseToken, String originalSignature, int numberOfDrops) {
        this.skuId = skuId;
        this.originalJson = originalJson;
        this.orderId = orderId;
        this.purchaseTime = purchaseTime;
        this.purchaseToken = purchaseToken;
        this.originalSignature = originalSignature;
        this.numberOfDrops = numberOfDrops;
    }
    
    public String getSkuId() {
        return skuId;
    }
    
    public String getOriginalJson() {
        return originalJson;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getPurchaseTime() {
        return purchaseTime;
    }
    
    public String getPurchaseToken() {
        return purchaseToken;
    }
    
    public String getOriginalSignature() {
        return originalSignature;
    }
    
    public int getNumberOfDrops() {
        return numberOfDrops;
    }
}
