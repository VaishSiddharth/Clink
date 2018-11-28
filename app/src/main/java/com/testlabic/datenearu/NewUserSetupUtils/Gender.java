package com.testlabic.datenearu.NewUserSetupUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    DatabaseReference reference;
    ProgressBar progressBar;
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        progressBar = findViewById(R.id.progress_bar);
        @SuppressLint("RestrictedApi") String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(uid);
        }
        final HashMap<String, Object> updateMale = new HashMap<>();
        final HashMap<String, Object> updateFemale = new HashMap<>();
        updateFemale.put("gender", Constants.FEMALE);
        updateMale.put("gender", Constants.MALE);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                reference.updateChildren(updateMale).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*
                        move to complexion
                         */
                        male.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(Gender.this, AboutYouEditor.class));
                    }
                });
            }
        });
        
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                reference.updateChildren(updateFemale).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        female.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(Gender.this, AboutYouEditor.class));
    
                    }
                });
            }
        });
    }
}
