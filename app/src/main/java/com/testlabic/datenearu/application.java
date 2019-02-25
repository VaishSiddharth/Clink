package com.testlabic.datenearu;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
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
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().goOnline();
        //setup test connection!
        setUpConnectionTest();
    }
    
    private void setUpConnectionTest() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("test");
        reference.setValue("Test");
    
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.e("App: ", "connected");
                } else {
                    Log.e("App: ", "not connected");
                }
            }
        
            @Override
            public void onCancelled(DatabaseError error) {
                //System.err.println("Listener was cancelled");
                Log.e("App: ", "On cancelled called!");
            }
        });
    }
    
    public static application getInstance()
    {
        return Instance;
    }
    
}
