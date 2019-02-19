package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;
import com.testlabic.datenearu.Utils.Constants;

public class PointLabelModel extends ViewModel {
    
    private static final DatabaseReference CITY_LABEL_REF =
            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.xPoints)
                    .child(Constants.uid);;
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(CITY_LABEL_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
