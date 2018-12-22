package com.testlabic.datenearu.HelpUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelMainResults {
    
    @SerializedName("next_page_token")
    private String  next_page_token;
    @SerializedName("results")
    private ArrayList<NearbyRestaurant> results;
    @SerializedName("status")
    private String status;
    
    public ModelMainResults(String next_page_token, ArrayList<NearbyRestaurant> results, String status) {
        this.next_page_token = next_page_token;
        this.results = results;
        this.status = status;
    }
    
    public String getNext_page_token() {
        return next_page_token;
    }
    
    public ArrayList<NearbyRestaurant> getResults() {
        return results;
    }
    
    public String getStatus() {
        return status;
    }
}
