package Fragments;

import android.os.Bundle;
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
import com.testlabic.datenearu.R;

import java.util.ArrayList;

import adapter.ProfileAdapter;
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
    private TextView username;
    private ImageView displayImage;
    
    Integer inbox[]={R.drawable.ic_inbox,R.drawable.ic_like,R.drawable.ic_profile,R.drawable.ic_settings};
    Integer arrow[]={R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp};
    String txttrades[]={"My Questions","Interests","Edit profile","Settings"};
    String txthistory[]={"Your questions for matches","Your interests","Change your profile details","Settings"};
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_profile, container, false);
        username = rootView.findViewById(R.id.username);
        displayImage = rootView.findViewById(R.id.display_image);
        /*
        fill profile details
         */
        
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
