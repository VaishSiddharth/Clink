package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;
import com.testlabic.datenearu.Utils.Constants;

public class DropsModel extends ViewModel {
    
    private DatabaseReference DROP_REF ;
    
    public void setRef(DatabaseReference reference) {
        DROP_REF = reference;
    }
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(DROP_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
