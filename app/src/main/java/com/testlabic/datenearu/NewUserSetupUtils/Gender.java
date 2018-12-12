package com.testlabic.datenearu.NewUserSetupUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;
public class Gender extends AppCompatActivity {
    Button male;
    Button female;
    Button intr_male;
    Button intr_female;
    DatabaseReference reference;
    Boolean genderSelected = false;
    Boolean iGenderSelected =false;
    ImageView next;
   
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        intr_female = findViewById(R.id.intr_female);
        intr_male = findViewById(R.id.intr_male);
        next = findViewById(R.id.next);
        
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
                    }
                });
            }
        });
        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iGenderSelected&&genderSelected)
                    startActivity(new Intent(Gender.this, AboutYouEditor.class));
                else
                    Toast.makeText(Gender.this, "Choose one from each first", Toast.LENGTH_SHORT).show();
            }
        });
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
    
}
