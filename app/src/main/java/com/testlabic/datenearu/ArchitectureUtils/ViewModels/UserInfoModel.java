package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;
import com.testlabic.datenearu.Utils.Constants;

public class UserInfoModel extends ViewModel {
    
    private static final DatabaseReference USER_INFO_REF =
            FirebaseDatabase.getInstance().getReference().child(Constants.userInfo).child(Constants.uid);
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(USER_INFO_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
    
    
}
