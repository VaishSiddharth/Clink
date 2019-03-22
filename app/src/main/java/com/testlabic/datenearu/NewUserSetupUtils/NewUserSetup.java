package com.testlabic.datenearu.NewUserSetupUtils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.Models.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class NewUserSetup extends AppCompatActivity implements StepperLayout.StepperListener {
    
    private static final String TAG = NewUserSetup.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 43;
    private StepperLayout mStepperLayout;
    boolean proceedAhead;
    ArrayList<ModelQuestion> questions;
    MyStepperAdapter adapter;
    private LocationManager mLocationManager = null;
    int maxSize = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_setup);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        setUpStatusbarColor();
        downloadQuestions();
        adapter = new MyStepperAdapter(getSupportFragmentManager(), this);
        adapter.createStep(0);
        adapter.createStep(1);
        adapter.createStep(2);
        adapter.createStep(3);
        adapter.createStep(4);
        mStepperLayout.setAdapter(adapter);
        setUpLocation();
        
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.newUserSetupDone, true).apply();
    }
    
    private void setUpStatusbarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appcolor));
            
        }
    }
    
    private void downloadQuestions() {
        // random list of
        questions = new ArrayList<>();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Questions);
        //do changes later..
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                //randomizing the questions, download all the questions in the list and then shuffle the list
                //and take first ten questions out of it!
                maxSize = (int) dataSnapshot.getChildrenCount();
                
                Log.e(TAG, "The max number of questions is  " + maxSize);
                if (maxSize < 1)
                    return;
                ArrayList<Integer> randomList = new ArrayList<>(maxSize);
                for (int i = 0; i < maxSize; i++)
                    randomList.add(i);
                Collections.shuffle(randomList);
                //now take first ten from these!
                for (int j = 0; j < Constants.questionsCount; j++) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.Questions)
                            .child(String.valueOf(randomList.get(j)));
                    Log.e(TAG, "The random value is " + randomList.get(j));
                    final int finalJ = j;
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                ModelQuestion question = dataSnapshot.getValue(ModelQuestion.class);
                                if (question != null) {
                                    //update questions here!
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.userInfo)
                                            .child(Constants.uid)
                                            .child(Constants.questions)
                                            .child(String.valueOf(finalJ));
                                    userRef.setValue(question);
                                    
                                }
                            }
                        }
                        
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        
                        }
                    });
                }
                
                Collections.shuffle(questions);
                
                if (questions.size() != 0) {
                    //push questions to node;
                    Constants.uid = FirebaseAuth.getInstance().getUid();
                    if (Constants.uid != null) {
                        Log.e(TAG, "Filling Questions!");
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.userInfo)
                                .child(Constants.uid)
                                .child(Constants.questions);
                        userRef.setValue(questions).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            
                            }
                        });
                    }
                    
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void setUpLocation() {
        
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(NewUserSetup.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // Log.e(TAG, "Displaying location settings...");
                    displayLocationSettingsRequest(NewUserSetup.this);
                } else {
                    //start location based service!
                    startService(new Intent(NewUserSetup.this, LocationUpdateService.class));
                }
            } else {
                ActivityCompat.requestPermissions(NewUserSetup.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                displayLocationSettingsRequest(NewUserSetup.this);
            } else {
                startService(new Intent(NewUserSetup.this, LocationUpdateService.class));
            }
            
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLocationSettingsRequest(NewUserSetup.this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(NewUserSetup.this, "Permission denied, enabling location services is mandatory to continue", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(NewUserSetup.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                /*
                start GPS Service here
                 */
                startService(new Intent(NewUserSetup.this, LocationUpdateService.class));
                
            } else {
                displayLocationSettingsRequest(NewUserSetup.this);
                Toast.makeText(NewUserSetup.this, "It is mandatory to enable locations to continue", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    @Override
    public void onCompleted(View completeButton) {
        if (proceedAhead)
            completeButton.setEnabled(true);
        else {
            completeButton.setEnabled(false);
            Toast.makeText(NewUserSetup.this, "Wait a while!", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onError(VerificationError verificationError) {
    
    }
    
    @Override
    public void onStepSelected(int newStepPosition) {
    
    }
    
    @Override
    public void onReturn() {
    
    }
    
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(NewUserSetup.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
    
}
