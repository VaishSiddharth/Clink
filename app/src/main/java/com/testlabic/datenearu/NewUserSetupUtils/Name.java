package com.testlabic.datenearu.NewUserSetupUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;
public class Name extends AppCompatActivity {
    EditText inputName, inputLastName;
    ImageView next;
    DatabaseReference reference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        setupWindowAnimations();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        next = findViewById(R.id.next);
        inputName = findViewById(R.id.input_name);
        inputLastName = findViewById(R.id.input_name_last);
        @SuppressLint("RestrictedApi") String uid = mAuth.getUid();
        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(uid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if(user!=null)
                    {
                        String firstName = user.getUserName();
                        String lastName = user.getUserLastName();
                        inputName.setText(firstName);
                        inputLastName.setText(lastName);
                    }
                }
    
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
        
                }
            });
           String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
         
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String name = inputName.getText().toString();
                String lastName = inputLastName.getText().toString();
                if(name.matches("") && lastName.matches(""))
                {
                    Toast.makeText(Name.this, "Your name can't be nothing, type it first", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(reference!=null)
                    {
                        HashMap<String, Object> userNameToUpdate = new HashMap<>();
                        HashMap<String, Object> userLastNameToUpdate = new HashMap<>();
                        userNameToUpdate.put("userName", name);
                        userLastNameToUpdate.put("userLastName", lastName);
                        reference.updateChildren(userLastNameToUpdate);
                        reference.updateChildren(userNameToUpdate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        /*
                                        move to gender activity
                                         */
                                        startActivity(new Intent(Name.this, Age.class));
                                    }
                                });
                    }
                }
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
