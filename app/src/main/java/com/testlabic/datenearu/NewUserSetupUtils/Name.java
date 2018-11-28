package com.testlabic.datenearu.NewUserSetupUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;
public class Name extends AppCompatActivity {
    EditText inputName;
    Button nextButton;
    DatabaseReference reference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        nextButton = findViewById(R.id.next_button);
        inputName = findViewById(R.id.input_name);
        @SuppressLint("RestrictedApi") String uid = mAuth.getUid();
        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(uid);
           String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
           inputName.setText(userName);
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String name = inputName.getText().toString();
                if(name.matches(""))
                {
                    Toast.makeText(Name.this, "Your name can't be nothing, type it first", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(reference!=null)
                    {
                        final ProgressBar bar = findViewById(R.id.progress_bar);
                        bar.setVisibility(View.VISIBLE);
                        nextButton.setEnabled(false);
                        HashMap<String, Object> userNameToUpdate = new HashMap<>();
                        userNameToUpdate.put("userName", name);
                        reference.updateChildren(userNameToUpdate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        bar.setVisibility(View.GONE);
                                        /*
                                        move to gender activity
                                         */
                                        nextButton.setEnabled(true);
                                        startActivity(new Intent(Name.this, Gender.class));
                                    }
                                });
                        
                    }
                }
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    finish();
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        Toast.makeText(Name.this, "Will just take a moment to register you", Toast.LENGTH_SHORT).show();
    }
}
