package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Age extends AppCompatActivity {
    
    ImageView next;
    int age = -1;
    private String outputDateStr = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);
        next = findViewById(R.id.next);
        setupWindowAnimations();
        LazyDatePicker lazyDatePicker = findViewById(R.id.lazyDatePicker);
        lazyDatePicker.setDateFormat(LazyDatePicker.DateFormat.MM_DD_YYYY);
        // lazyDatePicker.setMinDate(minDate);
        // lazyDatePicker.setMaxDate(maxDate);
        
        lazyDatePicker.setOnDatePickListener(new LazyDatePicker.OnDatePickListener() {
            @Override
            public void onDatePick(Date dateSelected) {
                //...
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                 outputDateStr = outputFormat.format(dateSelected);
                Log.e("Date is ", outputDateStr);
                
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
        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Age.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if(age!=-1)
                            editor.putInt(Constants.age,age ).apply();
                        startActivity(new Intent(Age.this, Gender.class));
                        reference.updateChildren(updateAgeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // save age to preferences
                                
                            }
                        });
                    }
                }
                
                else
                    Toast.makeText(Age.this, "Enter your date of birth first", Toast.LENGTH_SHORT).show();
                
            }
        });
        
        
    }
    private void setupWindowAnimations() {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
            
            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setReturnTransition(slide);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Constants.uid = firebaseAuth.getUid();
                }
            }
        });
    }
}
