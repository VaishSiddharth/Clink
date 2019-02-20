package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;
import com.testlabic.datenearu.Utils.Constants;

public class PrefsModel extends ViewModel {
    
    private static final DatabaseReference PREF_REF =
            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userPreferences)
                    .child(Constants.uid);;
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(PREF_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
