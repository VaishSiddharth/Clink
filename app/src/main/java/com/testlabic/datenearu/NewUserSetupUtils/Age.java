package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Age extends AppCompatActivity {
    
    ImageView next;
    private String outputDateStr = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);
        next = findViewById(R.id.next);
        
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
                
            }
        });
        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outputDateStr != null) {
                    String uid = Constants.uid;
                    if (uid != null) {
                        HashMap<String, Object> updateAgeMap = new HashMap<>();
                        updateAgeMap.put(Constants.dateOfBirth, outputDateStr);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.userInfo)
                                .child(uid);
                        Log.e("Age", "Click received");
                        reference.updateChildren(updateAgeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(Age.this, Gender.class));
                            }
                        });
                    }
                }
                
                else
                    Toast.makeText(Age.this, "Enter your date of birth first", Toast.LENGTH_SHORT).show();
                
            }
        });
    }
}
