package com.testlabic.datenearu;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;

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
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        FirebaseDatabase.getInstance().goOnline();
        //setup test connection!
    
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Constants.uid = firebaseAuth.getUid();
                   // Log.e("app", "Subscribed");
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.uid);
    
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.uid+"unread");
                }
            }
        };
        auth.addAuthStateListener(stateListener);
        setUpConnectionTest();
        if(Constants.uid!=null)
        AddDropsIfNewDay();
    }
    
    private void AddDropsIfNewDay() {
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
            .child(Constants.xPoints)
            .child(Constants.uid);
    
    reference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.getValue()!=null)
            {
                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                if (modelSubscr != null) {
                    if(modelSubscr.getLastUpdateTime()>0)
                    {
                        long lastTimeStamp = modelSubscr.getLastUpdateTime();
                        //if current timeStamp is greater than prev time by 24 hours, increase the drops
                        //Log.e("App ", lastTimeStamp+ " and current time "+ System.currentTimeMillis());
                        
                        int lastDrops = modelSubscr.getXPoints();
                        if(System.currentTimeMillis()- lastTimeStamp>24*60*60*1000)
                        {
                            //update
                           
                            HashMap<String, Object> dropsUpdate = new HashMap<>();
                            dropsUpdate.put(Constants.xPoints,lastDrops+Constants.dailyDrops );
                            reference.updateChildren(dropsUpdate);
                            
                            HashMap<String, Object> timeStampUpdate = new HashMap<>();
                            timeStampUpdate.put("lastUpdateTime", System.currentTimeMillis());
                            
                            reference.updateChildren(timeStampUpdate);
                            Log.e("App: ", "Updated drops");
                        }
                    }
                    else
                    {
                        //For First time
                        int lastDrops = modelSubscr.getXPoints();
                            //update
                     
                        HashMap<String, Object> dropsUpdate = new HashMap<>();
                        dropsUpdate.put(Constants.xPoints,lastDrops+Constants.dailyDrops );
                        reference.updateChildren(dropsUpdate);
    
                        HashMap<String, Object> timeStampUpdate = new HashMap<>();
                        timeStampUpdate.put("lastUpdateTime", System.currentTimeMillis());
    
                        reference.updateChildren(timeStampUpdate);
                        
                    }
                   
                }
            }
        }
    
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        
        }
    });
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
