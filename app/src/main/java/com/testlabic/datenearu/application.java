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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        //setup test connection!
        setUpConnectionTest();
    }
    
    private void setUpConnectionTest() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("test");
        reference.setValue("Test");
    }
    
    public static application getInstance()
    {
        return Instance;
    }
    
}
