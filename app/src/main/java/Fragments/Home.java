package Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.locationUpdater;

import java.util.ArrayList;

import adapter.Home_Adapter;
import adapter.TablayoutAdapter_Home;
import model.Home_Model;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    
    private static final String TAG = Home.class.getSimpleName();
    private String facebookUserId;
    
    public Home() {
        // Required empty public constructor
    }
    
    TextView changeLocation;
    View rootView;
    private Home_Adapter home_adapter;
    private RecyclerView recyclerview;
    private ArrayList<Home_Model> home_modelArrayList;
    
    Integer bitmap1[]={R.drawable.bitmap1,R.drawable.bitmap2,R.drawable.bitmap4,R.drawable.bitmap3};
    Integer imagers[]={R.drawable.ic_rupee,R.drawable.ic_rupee,R.drawable.ic_rupee,R.drawable.ic_rupee};
    String textdji[]={"Christine Miss","Marissa Williams","Teresa Duss","Rachel Moss"};
    String textprice[]={"24 yrs","22 yrs","22 yrs","23 yrs"};
    
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
           /*
        refresh current location when change location is tapped.
         */
        
        changeLocation = rootView.findViewById(R.id.changeLocation);
        
        putValueInchangeLocation();
        
        testApiCall();
        
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                startActivity(new Intent(getActivity(), locationUpdater.class));
                
            }
        });
    
        recyclerview = rootView.findViewById(R.id.recycler5);
        setUpRecyclerView();
        
        return rootView;
    }
    
    private void testApiCall() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
               /* make the API call */
                Bundle params  = new Bundle();
                params.putString("fields","images");
    
                for (UserInfo profile : user.getProviderData()) {
                    // check if the provider id matches "facebook.com"
                    if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                       
                        facebookUserId = profile.getUid();
                        Log.e(TAG, "The f id "+facebookUserId);
                    }
                }
    
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/"+facebookUserId+"/photos",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                /* You can parse this response using Json  */
                                Log.e(TAG, "Response is "+ response);
                            }
                        }
                ).executeAsync();
            }
        }
    
    
    private void setUpRecyclerView() {
      
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
    
        home_modelArrayList = new ArrayList<>();
    
        for (int i = 0; i < bitmap1.length; i++) {
            Home_Model view1 = new Home_Model(bitmap1[i],imagers[i],textdji[i],textprice[i]);
            home_modelArrayList.add(view1);
        }
        home_adapter = new Home_Adapter(getContext(),home_modelArrayList);
        recyclerview.setAdapter(home_adapter);
    
        
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
      
        putValueInchangeLocation();
        super.onResume();
        Log.e(TAG, "On resume called!");
    }
    
    
}
