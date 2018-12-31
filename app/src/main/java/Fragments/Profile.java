package Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.ProfileUtils.UploadPhotos;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;

import com.testlabic.datenearu.Adapters.ProfileAdapter;
import model.ProfileModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    
    public Profile() {
        // Required empty public constructor
    }
    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerview;
    private ArrayList<ProfileModel> profileModelArrayList;
    private  View rootView;
    private TextView username, desc;
    private ImageView displayImage;
    private TextView points, buy;
    
    Integer inbox[]={R.drawable.ic_inbox,R.drawable.ic_like,R.drawable.ic_profile,R.drawable.ic_settings};
    Integer arrow[]={R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp};
    String txttrades[]={"My Questions","Interests","About you","Settings"};
    String txthistory[]={"Your questions for matches","Your interests","Tap to Edit","Settings"};
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_profile, container, false);
        username = rootView.findViewById(R.id.username);
        desc = rootView.findViewById(R.id.about);
        displayImage = rootView.findViewById(R.id.display_image);
        points = rootView.findViewById(R.id.points);
        buy = rootView.findViewById(R.id.buy);
        
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PurchasePacks.class));
            }
        });
        
        setUpPoints();
        fillDesc();
        
        /*
        fill profile details
         */
        
        displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UploadPhotos.class);
                startActivity(i);
            }
        });
        
        fillProfile();
        
        recyclerview = rootView.findViewById(R.id.recycler1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        
        profileModelArrayList = new ArrayList<>();
        
        for (int i = 0; i < inbox.length; i++) {
            ProfileModel view = new ProfileModel(inbox[i],arrow[i],txttrades[i],txthistory[i]);
            profileModelArrayList.add(view);
        }
        profileAdapter = new ProfileAdapter(getActivity(),profileModelArrayList);
        recyclerview.setAdapter(profileAdapter);
        
        return rootView;
    }
    
    private void fillDesc() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                .child(Constants.uid)
                .child(Constants.oneLine);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                String text = dataSnapshot.getValue(String.class);
                desc.setText(text);
             }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
    }
    
    private void setUpPoints() {
        DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        xPointsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                if (modelSubscr != null) {
                    int current = modelSubscr.getXPoints();
                    String set = String.valueOf(current);
                    points.setText(set);

                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void fillProfile() {
    
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            if(user.getDisplayName()!=null)
                username.setText(user.getDisplayName());
            
            if(user.getPhotoUrl()!=null)
            {
                Glide.with(getContext()).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(displayImage);
            }
        }
        
    }
    
}
