package com.testlabic.datenearu.ArchitectureUtils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.testlabic.datenearu.ArchitectureUtils.FirebaseQueryLiveData;

public class UserInfoModel extends ViewModel {
    public static void Ref(DatabaseReference userInfoRef) {
        USER_INFO_REF = userInfoRef;
    }

    private static DatabaseReference USER_INFO_REF;
    
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(USER_INFO_REF);
    
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
    
    
}
