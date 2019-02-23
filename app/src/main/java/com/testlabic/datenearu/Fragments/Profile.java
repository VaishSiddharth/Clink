package com.testlabic.datenearu.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.ProfileUtils.UploadPhotos;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import com.testlabic.datenearu.Adapters.ProfileAdapter;

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    private View rootView;
    private TextView username, desc;
    private ImageView displayImage, edit;
    private TextView points, buy;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    
    Integer inbox[] = {R.drawable.ic_inbox, R.drawable.ic_like, R.drawable.ic_profile, R.drawable.ic_settings};
    Integer arrow[] = {R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp};
    String txttrades[] = {"My Questions", "Edit Profile", "About you", "Sign Out"};
    String txthistory[] = {"Your questions for matches", "Improve your profile", "Tap to Edit", "Logout from app"};
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        username = rootView.findViewById(R.id.username);
        desc = rootView.findViewById(R.id.about);
        edit = rootView.findViewById(R.id.edit);
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
            ProfileModel view = new ProfileModel(inbox[i], arrow[i], txttrades[i], txthistory[i]);
            profileModelArrayList.add(view);
        }
        profileAdapter = new ProfileAdapter(getActivity(), profileModelArrayList);
        recyclerview.setAdapter(profileAdapter);
        
        return rootView;
    }
    
    private void showEditDialog(String text) {
        
        final LayoutInflater factory = LayoutInflater.from(getActivity());
        final View editTextDialog = factory.inflate(R.layout.dialog_edittext, null);
        final EditText editText = editTextDialog.findViewById(R.id.ediText);
        editText.setText(text);
        
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                // .setTitleText("Send a one-time message (25 pts)?")
                .setCustomView(editTextDialog)
                .setConfirmButton("Update!", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        
                        //display edit text
                        String text = editText.getText().toString();
                        if (text.matches("")) {
                            Toast.makeText(getActivity(), "Can't be empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                                    .child(Constants.uid);
                            
                            final HashMap<String, Object> updateMap = new HashMap<>();
                            updateMap.put("oneLine", text);
                            
                            reference.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    
                                    //updateAgain
                                    sharedPreferences = getActivity().getSharedPreferences(Constants.userDetailsOff, Context.MODE_PRIVATE);
                                    final String cityLabel = sharedPreferences.getString(Constants.cityLabel, null);
                                    final String gender = sharedPreferences.getString(Constants.userGender, null);
                                    if (cityLabel != null) {
                                        if (gender == null) {
                                            DatabaseReference genderRef = FirebaseDatabase.getInstance().getReference()
                                                    .child(Constants.userInfo)
                                                    .child(Constants.uid).child("gender");
                                            genderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (dataSnapshot.getValue(String.class) != null) {
                                                            editor = sharedPreferences.edit();
                                                            editor.putString(Constants.userGender, dataSnapshot.getValue(String.class)).apply();
                                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                                                    .child(Constants.cityLabels).child(cityLabel).child(dataSnapshot.getValue(String.class)).child(Constants.uid);
                                                           // Log.e("Test", String.valueOf(reference1));
                                                            reference1.updateChildren(updateMap);
                                                        }
                                                    }
                                                }
                                                
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                
                                                }
                                            });
                                        } else {
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                                    .child(Constants.cityLabels).child(cityLabel).child(gender).child(Constants.uid);
                                            //Log.e("Test2", String.valueOf(reference1));
    
                                            reference1.updateChildren(updateMap);
                                        }
                                    }
                                    
                                    sDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                                    sDialog
                                            .setTitleText("Successful!")
                                            .setContentText("Info updated!")
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sDialog.dismissWithAnimation();
                                        }
                                    }, 2000);
                                    
                                }
                            });
                            //TODO: update other places also;
                            
                        }
                        
                    }
                })
                .show();
        ;
    }
    
    private void fillDesc() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                .child(Constants.uid)
                .child(Constants.oneLine);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                final String text = dataSnapshot.getValue(String.class);
                desc.setText(text);
                
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(text);
                        
                    }
                });
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
        if (user != null) {
            if (user.getDisplayName() != null)
                username.setText(user.getDisplayName());
            
            if (user.getPhotoUrl() != null) {
                Glide.with(getContext()).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(displayImage);
            }
        }
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(ModelUser.class) != null) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null && user.getUserName() != null && getContext() != null) {
                        Glide.with(getContext()).load(user.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(displayImage);
                        //Glide.with(Profile.this).load(user.getImageUrl()).into(displayImage);
                    }
                    
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
    }
    
}
