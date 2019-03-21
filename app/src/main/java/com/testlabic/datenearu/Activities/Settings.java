package com.testlabic.datenearu.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.testlabic.datenearu.Utils.Utils;

import java.util.HashMap;
import java.util.Set;

public class Settings extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = Settings.class.getSimpleName();
    public GoogleApiClient mGoogleApiClient;
    LinearLayout signOut, deleteAccount, shareApp;
    DatabaseReference reference;
    ValueEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        signOut = findViewById(R.id.sign_out);
        deleteAccount = findViewById(R.id.delete_account);
        shareApp = findViewById(R.id.share_app);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
    
        mGoogleApiClient = new GoogleApiClient.Builder(Settings.this)
                .enableAutoManage(Settings.this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Error occured "+connectionResult.getErrorMessage());
                    }
                })
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
            
                .build();
    
      
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating();
            }
        });
        
        setUpVisibility();
    }
    
    private void setUpVisibility() {
        final Switch visibilitySwitch = findViewById(R.id.visibility);
        SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
         final String gender = prefs.getString(Constants.userGender, null);
        if (gender != null) {
           
            reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.cityLabels)
                    .child(Constants.all_location)
                    .child(gender)
                    .child(Constants.uid);
         
            listener = reference.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (dataSnapshot.exists()) {
                         visibilitySwitch.setChecked(false);
                      }
                      else
                          visibilitySwitch.setChecked(true);
                  }
      
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
          
                  }
              });
    
            visibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        reference.setValue(null);
                    }
                    else
                    {
                        Toast.makeText(Settings.this, "Updating...", Toast.LENGTH_SHORT).show();
                        DuplicateUserInfoToCityLabel(reference);
                    }
                    
                }
            });
        }
    }
    
    private void DuplicateUserInfoToCityLabel(final DatabaseReference reference) {
        
        DatabaseReference initRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(Constants.uid);
        
        initRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                if(dataSnapshot.getValue(ModelUser.class)!=null)
                {
                    ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                    reference.setValue(modelUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Settings.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
        
    }
    
    public void rating()
    {
        //rating
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(5)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child("Feedbacks").child(Constants.uid);
                        final HashMap<String, Object> updatefeedback = new HashMap<>();
                        updatefeedback.put("review", feedback);
                        ref.updateChildren(updatefeedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                delete_Account();
                                Toast.makeText(getApplicationContext(),"We'll try to improve. Thanks",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).build();

        ratingDialog.show();
        TextView positive=ratingDialog.findViewById(R.id.dialog_rating_button_positive);
        if (positive != null) {
            positive.setVisibility(View.GONE);
        }
    
    }
    
    @Override
    protected void onStop() {
       
        if(reference!=null&&listener!=null)
            reference.removeEventListener(listener);
            super.onStop();
    }
    
    private void delete_Account() {
        final SweetAlertDialog alertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirm Deletion")
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setTitleText("Are you sure you want to leave us?")
                .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteuserInfo(sweetAlertDialog);
                    }
                });
        alertDialog.show();
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(Settings.this, R.drawable.button_4_dialogue));
        Button btn1 = alertDialog.findViewById(R.id.cancel_button);
        btn1.setBackground(ContextCompat.getDrawable(Settings.this, R.drawable.button_4_dialogue));
        btn.setTypeface(Utils.SFPRoLight(Settings.this));
        btn1.setTypeface(Utils.SFPRoLight(Settings.this));
    }
    
    
    private void deleteuserInfo(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Account Deleted!");
        sweetAlertDialog.setContentText("You'll be hidden from the users until you sign in again!");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels)
                .child(Constants.all_location)
                .child(Constants.uid)
                ;
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(Constants.uid)
                ;
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid)
                ;
        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.LikeInfo)
                .child(Constants.uid)
                ;
        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.DMIds)
                .child(Constants.uid)
                ;
        DatabaseReference reference6 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid)
                ;
        DatabaseReference reference7 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS)
                .child(Constants.uid)
                ;
    
        reference7.setValue(null);
        reference6.setValue(null);
    
        reference5.setValue(null);
        reference4.setValue(null);
    
        reference2.setValue(null);
        reference3.setValue(null);
        reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                    logout();
            }
        });
        //TODO: check all refrences and delete data!
        
    }
    
    public void logout() {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                
                FirebaseAuth.getInstance().signOut();
                if(LoginManager.getInstance()!=null)
                {
                    LoginManager.getInstance().logOut();
                }
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                    startActivity(new Intent(Settings.this, SignIn.class));
                            }
                        }
                    });
                }
            }
            
            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
        
        
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    
    }
}
