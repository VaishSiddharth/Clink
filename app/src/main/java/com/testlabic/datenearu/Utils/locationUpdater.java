package com.testlabic.datenearu.Utils;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.Models.LatLong;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class locationUpdater extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    
    private static final String TAG = locationUpdater.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 47;
    private GoogleApiClient mGoogleApiClient;
    String cityLabel;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_updater);
       final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                assert manager != null;
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.e(TAG, "Displaying location settings...");
                    displayLocationSettingsRequest(locationUpdater.this);
                }
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(locationUpdater.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(locationUpdater.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                    }
                }
                else buildGoogleApiClient();
    }
    
    
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        
        
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1500);
        /*
        DUMMY
         */
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
       String uid =  FirebaseAuth.getInstance().getUid();
        if(location!=null && location.getAccuracy()<3000&& uid!=null) {
    
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                String countryName = addresses.get(0).getCountryName();
                cityLabel = cityName+", "+stateName+", "+countryName;
    
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userInfo).child(uid);
                
                HashMap<String, Object> updateCityLabel = new HashMap<>();
                updateCityLabel.put("cityLabel", cityLabel);
                
                reference.updateChildren(updateCityLabel);
                
                
               // Log.e(TAG, "The city name will appear as : "+ cityName+","+stateName+","+countryName);
               
                
            } catch (IOException e) {
                e.printStackTrace();
            }
           
            
            LatLong latLong = new LatLong(location.getLongitude(), location.getLatitude());
    
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo).child(uid).child(Constants.location);
    
            reference.setValue(latLong).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                          /*
                            Duplicate user under the cityLabel node
                             */
                        cityLabel = cityLabel.replace(", ", "_");
                        DuplicateUserInfoToCityLabelNode(cityLabel);
                        Toast.makeText(locationUpdater.this, "Done!", Toast.LENGTH_SHORT).show();
                        /*
                        change the city label on main screen
                         */
                    }
                }
            });
        }
        else
            if(location!=null&&location.getAccuracy()>1000)
            {
                Toast.makeText(locationUpdater.this, "Waiting for accurate location...", Toast.LENGTH_SHORT).show();
            }
        
    }
    
    private void DuplicateUserInfoToCityLabelNode(final String cityLabel) {
        
        if(cityLabel!=null)
        {
            DatabaseReference refInit = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(Constants.uid);
            refInit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(ModelUser.class)!=null)
                    {
                        DatabaseReference refFin = FirebaseDatabase.getInstance().getReference().child(Constants.cityLabels)
                                .child(cityLabel).child(Constants.uid);
                        refFin.setValue(dataSnapshot.getValue(ModelUser.class)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        });
                    }
                }
    
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    
    private void displayLocationSettingsRequest(final Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1500);
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
                            status.startResolutionForResult(locationUpdater.this, REQUEST_CHECK_SETTINGS);
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
    
    
    @Override
    public void onConnectionSuspended(int i) {
    
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    
    }
    
    @Override
    public void onLocationChanged(Location location) {
        
        String uid = FirebaseAuth.getInstance().getUid();
        if(location!=null && location.getAccuracy()<100 && uid!=null) {
        
            LatLong latLong = new LatLong(location.getLongitude(), location.getLatitude());
        
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo).child(uid).child(Constants.location);
        
            reference.setValue(latLong).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        finish();
                        /*
                        change the city label on main screen
                         */
                    }
                }
            });
        }
        else
        if(location!=null&&location.getAccuracy()>100)
        {
            Toast.makeText(locationUpdater.this, "Waiting for accurate location...", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
}
