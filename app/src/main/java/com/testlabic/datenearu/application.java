package com.testlabic.datenearu;

import android.app.Application;
import android.os.Handler;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;

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
    }
    
    public static application getInstance()
    {
        return Instance;
    }
    
}
