package com.testlabic.datenearu.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.locationUpdater;

import java.util.ArrayList;

import com.testlabic.datenearu.Adapters.Home_Adapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    
    private static final String TAG = Home.class.getSimpleName();
    private String facebookUserId;
    private TextView notifCount;
    int count = 0;
    ImageView notification;
    
    public Home() {
        // Required empty public constructor
    }
    
    TextView changeLocation;
    View rootView;
    private Home_Adapter home_adapter;
    private RecyclerView recyclerview;
    private ArrayList<ModelUser> displayArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
           /*
        refresh current location when change location is tapped.
         */
        notification = rootView.findViewById(R.id.notification);
       /* if (Constants.uid != null)
            checkForNotification();*/
        
        changeLocation = rootView.findViewById(R.id.changeLocation);
        notifCount = rootView.findViewById(R.id.notifCount);
        putValueInchangeLocation();
        
        //testApiCall();
        
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                startActivity(new Intent(getActivity(), locationUpdater.class));
            }
        });
        recyclerview = rootView.findViewById(R.id.recycler5);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        
        setUpRecyclerView();
        
        return rootView;
    }
    
    private void testApiCall() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            /* make the API call */
            Bundle params = new Bundle();
            params.putString("fields", "images");
            
            for (UserInfo profile : user.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    
                    facebookUserId = profile.getUid();
                    Log.e(TAG, "The f id " + facebookUserId);
                }
            }
            
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + facebookUserId + "/photos",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            /* You can parse this response using Json  */
                            Log.e(TAG, "Response is " + response);
                        }
                    }
            ).executeAsync();
        }
    }
    
    private void setUpRecyclerView() {
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String city = preferences.getString(Constants.cityLabel, "Gwalior_Madhya Pradesh_India");
        
        displayArrayList = new ArrayList<>();
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels).child(city);
        
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                if (dataSnapshot.getValue() != null) {
                    ModelUser item = dataSnapshot.getValue(ModelUser.class);
                    if (item != null)
                        displayArrayList.add(item);
                }
            }
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                home_adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                home_adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                populate adapter...
                 */
                home_adapter = new Home_Adapter(getContext(), displayArrayList);
                recyclerview.setAdapter(home_adapter);
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
    }
    
    private void putValueInchangeLocation() {
        
        if (Constants.uid != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo).child(Constants.uid).child("cityLabel");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if (dataSnapshot.getValue() != null) {
                        String value = dataSnapshot.getValue(String.class);
                        changeLocation.setText(value);
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        putValueInchangeLocation();
        //checkForNotification();
        
       // Log.e(TAG, "On resume called!");
    }
    
}
