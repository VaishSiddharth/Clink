package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;

public class DropsModel extends ViewModel {
    
    private DatabaseReference DROP_REF ;
    
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(DROP_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
