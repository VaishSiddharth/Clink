package com.testlabic.datenearu.Utils;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.ArchitectureUtils.ViewModels.CityLabelModel;
import com.testlabic.datenearu.ArchitectureUtils.ViewModels.UserInfoModel;
import com.testlabic.datenearu.Models.LatLong;
import com.testlabic.datenearu.Models.ModelCityLabel;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.NewUserSetupUtils.NewUserSetup;
import com.testlabic.datenearu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class locationUpdater extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    
    private static final String TAG = locationUpdater.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 47;
    private GoogleApiClient mGoogleApiClient;
    String gender;
    String cityLabel;
    ListView listView;
    ArrayList<ModelCityLabel> list;
    TextView updatingLocationLabel;
    GoogleProgressBar googleProgressBar;
    LinearLayout myCurrentLocation;
    SweetAlertDialog alertDialog;
    DatabaseReference cityLabelRef;
    DatabaseReference adapterRef;
    ChildEventListener childEventListener;
    HashMap<String, Object> updateCityLabel;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_updater);
        SharedPreferences preferences = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
        myCurrentLocation = findViewById(R.id.my_location_ll);
        googleProgressBar = findViewById(R.id.google_progress);
        updatingLocationLabel = findViewById(R.id.updatingLocation);
        listView = findViewById(R.id.listView);
        alertDialog = new SweetAlertDialog(locationUpdater.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Updating")
                .setContentText(".......");
        populateListView();
       
        myCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleProgressBar.setVisibility(View.VISIBLE);
                setUpLocationService();
            }
        });
        gender = preferences.getString(Constants.userGender, "male");
    }
    
    private void cleanUp()
    {
        if(adapterRef!=null&&childEventListener!=null)
            adapterRef.removeEventListener(childEventListener);
    }
    
    private void populateListView() {
        
        adapterRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels);
        list = new ArrayList<>();
        childEventListener = adapterRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String cityName = dataSnapshot.getKey();
                ModelCityLabel label = new ModelCityLabel(cityName, 0);
                list.add(label);
            }
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            
            }
            
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
        adapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                Adapter adapter = new Adapter(locationUpdater.this, list);
                
                listView.setAdapter(adapter);
                
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alertDialog.show();
                        cityLabel = list.get(position).getCityLabel();
                        cityLabel = cityLabel.replace(", ", "_");
                        updateCityLabel = new HashMap<>();
                        updateCityLabel.put("cityLabel", cityLabel);
                        updateCityLabelAndData();
                    }
                });
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void setUpLocationService() {
        //
    
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
       
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(locationUpdater.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.e(TAG, "Displaying location settings...");
                    displayLocationSettingsRequest(locationUpdater.this);
                }
            }
            else
                {
                    ActivityCompat.requestPermissions(locationUpdater.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
        } else {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                displayLocationSettingsRequest(locationUpdater.this);
            }
        }
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
        alertDialog = new SweetAlertDialog(locationUpdater.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Updating")
                .setContentText(".......");
        alertDialog.show();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        String uid = FirebaseAuth.getInstance().getUid();
        Constants.uid = uid;
        if (location != null && location.getAccuracy() < 5000 && uid != null) {
            alertDialog.show();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                String countryName = addresses.get(0).getCountryName();
                cityLabel = cityName + ", " + stateName + ", " + countryName;
                Log.e(TAG, "City Label from location cordinates is "+cityLabel);
                updateCityLabel = new HashMap<>();
                updateCityLabel.put("cityLabel", cityLabel);
                updateLocationCoordinates(location);
                
                //UserInfoModel userInfoModel = ViewModelProviders.of(this).get(UserInfoModel.class);
                
                //LiveData<DataSnapshot> liveData = userInfoModel.getDataSnapshotLiveData();
                updateCityLabelAndData();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
    }
    
    private void updateCityLabelAndData() {
    
        Log.e(TAG, "Update city label and data called!");
        
        CityLabelModel viewModel = ViewModelProviders.of(this).get(CityLabelModel.class);
    
        LiveData<DataSnapshot> cityLiveData = viewModel.getDataSnapshotLiveData();
    
        cityLabelRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(Constants.uid);
    
        cityLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null)
                {
                    if (dataSnapshot.getValue() == null) {
                    
                        cityLabelRef.updateChildren(updateCityLabel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                cityLabel = cityLabel.replace(", ", "_");
                                DuplicateUserInfoToCityLabelNode(cityLabel);
                            }
                        });
                    } else {
                        String previousCityLabel = dataSnapshot.getValue(String.class);
                        if (previousCityLabel != null && !(previousCityLabel.equals(""))) {
                            previousCityLabel = previousCityLabel.replace(", ", "_");
                            Log.e(TAG, "Previous city label is "+previousCityLabel);
                            DatabaseReference prevRef = FirebaseDatabase.getInstance().getReference()
                                    .child(Constants.cityLabels).child(previousCityLabel).child(gender).child(Constants.uid);
                            prevRef.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    cityLabelRef.updateChildren(updateCityLabel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            cityLabel = cityLabel.replace(", ", "_");
                                            Log.e(TAG, "Calling duplicate user info method");
                                            DuplicateUserInfoToCityLabelNode(cityLabel);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    
    private void updateLocationCoordinates(Location location) {
        LatLong latLong = new LatLong(location.getLongitude(), location.getLatitude());
    
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(Constants.uid).child(Constants.location);
    
        reference.setValue(latLong);
    }
    
    private void DuplicateUserInfoToCityLabelNode(final String cityLabel) {
        
        if (cityLabel != null) {
            //change code to viewmodel livedata architecture;
            UserInfoModel userInfoModel = ViewModelProviders.of(this).get(UserInfoModel.class);
            
            LiveData<DataSnapshot> liveData = userInfoModel.getDataSnapshotLiveData();
            
            liveData.observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                    
                    if (dataSnapshot != null) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            user.setQuestions(null);
                            gender = user.getGender();
                            DatabaseReference refFin = FirebaseDatabase.getInstance().getReference().child(Constants.cityLabels)
                                    .child(cityLabel).child(gender).child(Constants.uid);
                            refFin.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //update_offline_location
                                    SharedPreferences preferences = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(Constants.cityLabel, cityLabel).apply();
                                    Log.v(TAG, "Updated citylabel offline!");
                                    if (alertDialog != null)
                                        alertDialog.hide();
                                    finish();
                                }
                            });
                        }
                    }
                    
                }
            });
        }
    }
    
    private void displayLocationSettingsRequest(final Context context) {
        //googleProgressBar.setVisibility(View.VISIBLE);
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
                        Log.i(TAG, "AllMessagesList location settings are satisfied.");
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
        if (location != null && location.getAccuracy() < 3000 && uid != null) {
            updateLocationCoordinates(location);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        cleanUp();
    }
    
    private class Adapter extends BaseAdapter {
        Context context;
        ArrayList<ModelCityLabel> list;
        
        public Adapter(Context context, ArrayList<ModelCityLabel> list) {
            this.context = context;
            this.list = list;
        }
        
        @Override
        public int getCount() {
            return list.size();
        }
        
        @Override
        public ModelCityLabel getItem(int position) {
            return list.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return 0;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.sample_citylables, null);
            }
            
            TextView cityLabel = convertView.findViewById(R.id.cityName);
            cityLabel.setText(getItem(position).getCityLabel().replace("_", ", "));
            return convertView;
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
