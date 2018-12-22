package com.testlabic.datenearu.Models;

import com.testlabic.datenearu.HelpUtils.ModelMainResults;
import com.testlabic.datenearu.HelpUtils.NearbyRestaurant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
 //https://maps.googleapis.com/maps/api/place
// /nearbysearch/json?location=26.7824186,80.9285919&radius=8000&type=restaurant
// &keyword=cruise&key=AIzaSyARxNBfzDHLGchcheXnd7_w70UynPSKnCU
 
public interface ApiInterface {
    
    @GET("maps/api/place/nearbysearch/json")
    Call<ModelMainResults> getNearByResults(@Query("location") String location, @Query("radius") Integer radius, @Query("type") String types , @Query("key") String key);
    
}