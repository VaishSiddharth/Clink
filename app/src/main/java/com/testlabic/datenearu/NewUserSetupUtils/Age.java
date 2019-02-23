package com.testlabic.datenearu.NewUserSetupUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Age extends Fragment implements BlockingStep {
    
    ImageView next;
    int age = -1;
    private String outputDateStr = null;
    private SweetAlertDialog dialog;
    
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_age, container, false);
    
        LazyDatePicker lazyDatePicker = rootView.findViewById(R.id.lazyDatePicker);
        lazyDatePicker.setDateFormat(LazyDatePicker.DateFormat.MM_DD_YYYY);
        // lazyDatePicker.setMinDate(minDate);
        // lazyDatePicker.setMaxDate(maxDate);
    
        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");
        
        lazyDatePicker.setOnDatePickListener(new LazyDatePicker.OnDatePickListener() {
            @Override
            public void onDatePick(Date dateSelected) {
                //...
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                outputDateStr = outputFormat.format(dateSelected);
                Log.e("Date is ", outputDateStr);
                hideKeyboard(getActivity());
                
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            
                if (outputDateStr.length() > 4) {
                    int birthYear = Integer.parseInt(outputDateStr.substring(outputDateStr.length() - 4));
                    age = currentYear - birthYear;
                    String uid = FirebaseAuth.getInstance().getUid();
                    HashMap<String, Object> updateAgeMap = new HashMap<>();
                    updateAgeMap.put(Constants.numeralAge, age);
                    if (uid != null) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.userInfo)
                                .child(uid);
                        reference.updateChildren(updateAgeMap);
                    }
                } else {
                    // whatever is appropriate in this case
                    throw new IllegalArgumentException("Invalid date");
                }
            
            }
        });
        
        return rootView;
    }
    
   
    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        dialog.show();
        if (outputDateStr != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            Log.e("Age", "uid is "+uid);
            if (uid != null) {
                HashMap<String, Object> updateAgeMap = new HashMap<>();
                updateAgeMap.put(Constants.dateOfBirth, outputDateStr);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userInfo)
                        .child(uid);
                Log.e("Age", "Click received");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.userDetailsOff, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(age!=-1)
                    editor.putInt(Constants.age,age ).apply();
                reference.updateChildren(updateAgeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    
                    callback.goToNextStep();
                    dialog.dismiss();
                    }
                });
            }
        }
    
        else {
            dialog.dismiss();
            Toast.makeText(getActivity(), "Enter your date of birth first", Toast.LENGTH_SHORT).show();
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
