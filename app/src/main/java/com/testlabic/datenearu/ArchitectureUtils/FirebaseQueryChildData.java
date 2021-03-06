package com.testlabic.datenearu.ArchitectureUtils;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class FirebaseQueryChildData  extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";
    
    private final Query query;
    private final MyChildEventListener listener = new MyChildEventListener();
    
    public FirebaseQueryChildData(Query query) {
        this.query = query;
    }
    
    public FirebaseQueryChildData(DatabaseReference ref) {
        this.query = ref;
    }
    
    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        query.addChildEventListener(listener);
    }
    
    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        query.removeEventListener(listener);
    }
    
    private class MyChildEventListener implements ChildEventListener {
        
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);
        }
    
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);
        }
    
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        
        }
    
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        
        }
    
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
