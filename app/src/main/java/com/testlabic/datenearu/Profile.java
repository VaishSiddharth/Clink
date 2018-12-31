package com.testlabic.datenearu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.testlabic.datenearu.Adapters.ProfileAdapter;
import model.ProfileModel;

public class Profile extends AppCompatActivity {

    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerview;
    private ArrayList<ProfileModel> profileModelArrayList;
    private TextView userName;
    private ImageView displayImage;
    TextView buy, points;

    Integer inbox[]={R.drawable.ic_inbox,R.drawable.ic_headphones,R.drawable.ic_like,R.drawable.ic_star,R.drawable.ic_profile,R.drawable.ic_settings};
    Integer arrow[]={R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp};
    String txttrades[]={"My Trades","Help Center","Favorites","Ratings","My Profile","Settings"};
    String txthistory[]={"Check your trading history","Help regarding your recent trades","Your collection","Your collection","Change your profile details","Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*
        fill up profile details
         */
        
        
        recyclerview = findViewById(R.id.recycler1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Profile.this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        profileModelArrayList = new ArrayList<>();

        for (int i = 0; i < inbox.length; i++) {
            ProfileModel view = new ProfileModel(inbox[i],arrow[i],txttrades[i],txthistory[i]);
            profileModelArrayList.add(view);
        }
        profileAdapter = new ProfileAdapter(Profile.this,profileModelArrayList);
        recyclerview.setAdapter(profileAdapter);
    }
}
