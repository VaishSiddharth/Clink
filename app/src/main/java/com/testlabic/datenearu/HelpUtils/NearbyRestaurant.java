package com.testlabic.datenearu.HelpUtils;

import com.google.gson.annotations.SerializedName;

public class NearbyRestaurant {
    
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("icon")
    private String icon;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("rating")
    private String rating;
    @SerializedName("price_level")
    private String price_level;
    
    public NearbyRestaurant() {
    }
    
    public NearbyRestaurant(String name, String id, String icon, String vicinity, String rating, String price_level) {
        this.name = name;
        this.id = id;
        this.icon = icon;
        this.vicinity = vicinity;
        this.rating = rating;
        this.price_level = price_level;
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getVicinity() {
        return vicinity;
    }
    
    public String getRating() {
        return rating;
    }
    
    public String getPrice_level() {
        return price_level;
    }
}
