package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;
import com.testlabic.datenearu.Utils.Constants;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class OnlineStatusModel extends ViewModel {
    private DatabaseReference STAT_REF  = FirebaseDatabase.getInstance().getReference()
            .child(Constants.usersStatus);
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(STAT_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
