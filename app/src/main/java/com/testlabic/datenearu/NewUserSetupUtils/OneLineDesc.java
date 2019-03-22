package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneLineDesc extends Fragment implements BlockingStep {
    
    private SweetAlertDialog dialog;
    
    public OneLineDesc() {
        // Required empty public constructor
    }
    
    View rootView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_one_line_desc, container, false);
        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");
        
        return rootView;
    }
    
    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
    
    }
    
    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        dialog.show();
        EditText editText = rootView.findViewById(R.id.desc);
        String text = editText.getText().toString();
    
        if(text.matches("")) {
            Toast.makeText(getActivity(), "Write something please!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else
        {
            //update the database!
        
            String uid = FirebaseAuth.getInstance().getUid();
            HashMap<String, Object> updateAgeMap = new HashMap<>();
            updateAgeMap.put(Constants.oneLine, text);
            if (uid != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userInfo)
                        .child(uid);
                reference.updateChildren(updateAgeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UpdateXPoints();
                        DuplicateUserInfo();
                        dialog.dismiss();
                    }
                });
            }
        }
    
    }
    
    private void UpdateXPoints() {
        Constants.uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        HashMap<String, Object> updatePoints = new HashMap<>();
        updatePoints.put(Constants.xPoints, Constants.newUserDrops);
        reference.updateChildren(updatePoints);
    }
    
    private void DuplicateUserInfo() {
        
        DatabaseReference refInit = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                .child(Constants.uid);
        refInit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(ModelUser.class) != null) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null) {
                        String gender = user.getGender();
                        String cityLabel = user.getCityLabel();
                        if (cityLabel != null && gender != null) {
                            cityLabel = cityLabel.replace(", ", "_");
                            DatabaseReference refFin = FirebaseDatabase.getInstance().getReference().child(Constants.cityLabels)
                                    .child(cityLabel).child(gender).child(Constants.uid);
                            final String finalCityLabel = cityLabel;
                            refFin.setValue(dataSnapshot.getValue(ModelUser.class)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(Constants.cityLabel, finalCityLabel).apply();
                                    dialog.dismiss();
                                    startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            |Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            });
                        }
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    
    
    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
            callback.goToPrevStep();
    }
    
    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }
    
    @Override
    public void onSelected() {
    
    }
    
    @Override
    public void onError(@NonNull VerificationError error) {
    
    }
}
