package com.testlabic.datenearu;

import android.app.Application;
import android.os.Handler;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

public class application extends Application {
    private static application Instance;
    
    public static volatile Handler applicationHandler = null;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Instance=this;
    
        applicationHandler = new Handler(Instance.getMainLooper());
    
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    
   
    
    public static application getInstance()
    {
        return Instance;
    }
    
}
