package com.testlabic.datenearu.Utils;

import android.Manifest;
import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.ArchitectureUtils.ViewModels.CityLabelModel;
import com.testlabic.datenearu.ArchitectureUtils.ViewModels.UserInfoModel;
import com.testlabic.datenearu.Models.LatLong;
import com.testlabic.datenearu.Models.ModelCityLabel;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class locationUpdater extends AppCompatActivity {
    
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
    
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    
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
                alertDialog.show();
                initializeLocationManager();
                setUpLocationService();
    
            }
        });
        gender = preferences.getString(Constants.userGender, "male");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (alertDialog != null)
            alertDialog.dismiss();
    }
    
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    
    private void cleanUp() {
        if (adapterRef != null && childEventListener != null)
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
                        alertDialog = new SweetAlertDialog(locationUpdater.this, SweetAlertDialog.PROGRESS_TYPE)
                                .setTitleText("Updating")
                                .setContentText(".......");
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
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(locationUpdater.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // Log.e(TAG, "Displaying location settings...");
                    displayLocationSettingsRequest(locationUpdater.this);
                } else {
                    //start location based service!
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                            mLocationListeners[1]);
                }
            } else {
                ActivityCompat.requestPermissions(locationUpdater.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                displayLocationSettingsRequest(locationUpdater.this);
            } else {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            }
            
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        // Log.e(TAG, "Displaying location settings...");
                        displayLocationSettingsRequest(locationUpdater.this);
                    } else {
                        //start location based service!
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mLocationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                                mLocationListeners[1]);
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(locationUpdater.this, "Permission denied, enabling location services is mandatory to continue", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(locationUpdater.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHECK_SETTINGS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        }
    }
    
    private void updateCityLabelAndData() {
        
        // Log.e(TAG, "Update city label and data called!");
        
        CityLabelModel viewModel = ViewModelProviders.of(this).get(CityLabelModel.class);
        
        LiveData<DataSnapshot> cityLiveData = viewModel.getDataSnapshotLiveData();
        
        cityLabelRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(Constants.uid);
        
        cityLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
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
                            //Log.e(TAG, "Previous city label is "+previousCityLabel);
                            DatabaseReference prevRef = FirebaseDatabase.getInstance().getReference()
                                    .child(Constants.cityLabels).child(previousCityLabel).child(gender).child(Constants.uid);
                            prevRef.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    cityLabelRef.updateChildren(updateCityLabel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            cityLabel = cityLabel.replace(", ", "_");
    
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
        Log.e(TAG, "Calling duplicate user info method");
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
                                    //Log.e(TAG, "Updated citylabel!");
                                    if (alertDialog != null)
                                        alertDialog.dismiss();
    
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra(Constants.refresh, true);
                                    setResult(Activity.RESULT_OK, resultIntent);
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
                        //start location based service
                        if (ActivityCompat.checkSelfPermission(locationUpdater.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(locationUpdater.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mLocationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                                mLocationListeners[1]);
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
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
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
    
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    
    
    
    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;
        
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
    
            if (location != null && location.getAccuracy() < 5000 && Constants.uid != null) {
                alertDialog.show();
                Geocoder geocoder = new Geocoder(locationUpdater.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String cityName = addresses.get(0).getLocality();
                    String stateName = addresses.get(0).getAdminArea();
                    String countryName = addresses.get(0).getCountryName();
                    cityLabel = cityName + "_" + stateName + "_" + countryName;
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
                updateLocationCoordinates(location);
            }
        }
        
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    
}
