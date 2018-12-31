package com.testlabic.datenearu.NewUserSetupUtils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneLineDesc extends Fragment implements BlockingStep {
    
    public OneLineDesc() {
        // Required empty public constructor
    }
    
    View rootView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_one_line_desc, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        
        return rootView;
    }
    
    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        EditText editText = rootView.findViewById(R.id.desc);
        String text = editText.getText().toString();
        
        if(text.matches(""))
            Toast.makeText(getActivity(), "Write something please!", Toast.LENGTH_SHORT).show();
        
        else
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
                            callback.goToNextStep();
                    }
                });
            }
        }
        
    }
    
    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
    
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