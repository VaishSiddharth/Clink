package com.testlabic.datenearu;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;

public class application extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
    
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
