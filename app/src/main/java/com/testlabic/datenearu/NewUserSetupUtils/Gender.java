package com.testlabic.datenearu.NewUserSetupUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.Models.ModelPrefs;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Gender extends Fragment implements BlockingStep {
    Button male;
    Button female;
    Button intr_male;
    Button intr_female;
    DatabaseReference reference;
    Boolean genderSelected = false;
    Boolean iGenderSelected =false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ImageView next;
    private SweetAlertDialog dialog;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_gender, container, false);
    
        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        male = rootView.findViewById(R.id.male);
        female = rootView.findViewById(R.id.female);
        intr_female = rootView.findViewById(R.id.intr_female);
        intr_male = rootView.findViewById(R.id.intr_male);
        next = rootView.findViewById(R.id.next);
        @SuppressLint("RestrictedApi") String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(uid);
        }
        final HashMap<String, Object> updateMale = new HashMap<>();
        final HashMap<String, Object> updateFemale = new HashMap<>();
        updateFemale.put("gender", Constants.FEMALE);
        updateMale.put("gender", Constants.MALE);
    
        final HashMap<String, Object> updateMaleI = new HashMap<>();
        final HashMap<String, Object> updateFemaleI = new HashMap<>();
        updateFemaleI.put("interestedIn", Constants.FEMALE);
        updateMaleI.put("interestedIn", Constants.MALE);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSelected = false;
                unColor(female);
                color(male);
                reference.updateChildren(updateMale).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        genderSelected = true;
                        editor.putString(Constants.userGender, Constants.male).apply();
                    }
                });
            }
        });
    
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSelected = false;
                color(female);
                unColor(male);
                reference.updateChildren(updateFemale).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        genderSelected = true;
                        editor.putString(Constants.userGender, Constants.female).apply();
                    }
                });
            }
        });
    
        intr_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGenderSelected = false;
                unColor(intr_female);
                color(intr_male);
                reference.updateChildren(updateMaleI).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iGenderSelected = true;
                        editor.putString(Constants.userIntrGender, Constants.male).apply();
                    }
                });
            }
        });
    
        intr_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGenderSelected = false;
                color(intr_female);
                unColor(intr_male);
                reference.updateChildren(updateFemaleI).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iGenderSelected = true;
                        editor.putString(Constants.userIntrGender, Constants.female).apply();
                    }
                });
            }
        });
    
      
        return rootView;
    }
    
   
    private void setUpDefaultUserPreferences() {
        
        Constants.uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userPreferences)
                .child(Constants.uid);
        // calculate min Age, max age according to your algo
        int userAge = sharedPreferences.getInt(Constants.age, -1);
        int minAge =-1, maxAge =-1;
        String gender = sharedPreferences.getString(Constants.userGender, null);
        String preferredGender = sharedPreferences.getString(Constants.userIntrGender, null);
        if(gender.equals(Constants.male)) {
            minAge = userAge<23?18: userAge-5;
            maxAge = userAge;
        }
        else
        {
            maxAge = userAge+5;
            minAge = userAge;
        }
        ModelPrefs prefs = new ModelPrefs(15.0, minAge, maxAge, preferredGender );
        reference.setValue(prefs);
        
        
    }
    
    private void unColor(Button button) {
        button.setBackground(getResources().getDrawable(R.drawable.border_black_box));
        button.setTextColor(getResources().getColor(R.color.light_black));
    }
    
    private void color (Button button)
    {
        button.setBackground(getResources().getDrawable(R.drawable.full_black_box));
        button.setTextColor(getResources().getColor(R.color.white));
    }
  
    
    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if(iGenderSelected&&genderSelected) {
            dialog.show();
            setUpDefaultUserPreferences();
           
           callback.goToNextStep();
            dialog.dismiss();
        } else {
            Toast.makeText(getActivity(), "Choose one from each first", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
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
