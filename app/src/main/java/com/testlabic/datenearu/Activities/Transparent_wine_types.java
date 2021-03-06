package com.testlabic.datenearu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.AttemptMatchUtils.MatchCalculator;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Utils;

import java.util.Date;
import java.util.HashMap;

public class Transparent_wine_types extends Activity {
    
    String clickedUsersId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wine_categories);
        clickedUsersId = getIntent().getStringExtra(Constants.clickedUid);
    
        TextView one, two, three;
        ImageView rg, pr, ry;
    
        rg=findViewById(R.id.rgbottle);
        pr =findViewById(R.id.prbottle);
        ry = findViewById(R.id.rybottle);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
    
        rg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateSendingWine(200);
            }
        });
        
        pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateSendingWine(500);
            }
        });
        
        ry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateSendingWine(1000);
            }
        });
       one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateSendingWine(200);
            }
        });
    
       two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateSendingWine(500);
            }
        });
    
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initateSendingWine(1000);
            }
        });
    }
    
    
    private void initateSendingWine(final int wineAmount) {
        
        //complete the transaction and update accordingly
        
        final DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        
        xPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Transparent_wine_types.this, SweetAlertDialog.PROGRESS_TYPE)
                        .setContentText("Loading.");
                sweetAlertDialog.show();
                
    
                TextView title = sweetAlertDialog.findViewById(R.id.title_text);
                if(title!=null)
                    title.setTypeface(Utils.SFProRegular(Transparent_wine_types.this));
    
                TextView contentText = sweetAlertDialog.findViewById(R.id.content_text);
                if(contentText!=null)
                    contentText.setTypeface(Utils.SFPRoLight(Transparent_wine_types.this));
                
                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                if (modelSubscr != null) {
                    int current = modelSubscr.getXPoints();
                    if (current < wineAmount) {
                        Toast.makeText(Transparent_wine_types.this, "You don't have enough drops, buy now!", Toast.LENGTH_SHORT).show();
                        BuyPoints();
                    } else {
                        current -= wineAmount;
                        HashMap<String, Object> updatePoints = new HashMap<>();
                        updatePoints.put(Constants.xPoints, current);
                        dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sweetAlertDialog
                                        .setTitleText("Sending!")
                                        .setContentText("Your wine is on its way")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.findViewById(R.id.confirm_button).setVisibility(View.GONE);
                                //sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                                sendWine(wineAmount);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(Transparent_wine_types.this,MainActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        sweetAlertDialog.dismiss();
                                        finish();
                                    }
                                }, 2500);
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
    
    private void sendWine(final int wineAmount) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(clickedUsersId).child(Constants.unread).push();
        
        String wineType = Constants.regularWine;
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        //HashMap<String, Object > timeStamp = new HashMap<>();
        //timeStamp.put(Constants.timeStamp, ServerValue.TIMESTAMP);
        long timeStamp = -1 * new Date().getTime();
        String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        switch (wineAmount)
        {
            case 200: wineType = Constants.regularWine;
                break;
            case 500: wineType = Constants.premiumWine;
                break;
            case 1000: wineType = Constants.royalWine;
                break;
        }
        ModelGift gift = new ModelGift(Constants.uid, wineType, clickedUsersId, userName, url, timeStamp );
        reference.setValue(gift).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //add 300 drops to receivers
                
                final DatabaseReference DROP_REF =
                        FirebaseDatabase.getInstance().getReference().child(Constants.xPoints).child(clickedUsersId);
                
                DROP_REF.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null)
                        {
                            ModelSubscr subscr = dataSnapshot.getValue(ModelSubscr.class);
                            if(subscr!=null)
                            {
                                int current = subscr.getXPoints();
                                current += wineAmount;
                                HashMap<String, Object> updatePoints = new HashMap<>();
                                updatePoints.put(Constants.xPoints, current);
                                DROP_REF.updateChildren(updatePoints);
                            }
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    
                    }
                });
            }
        });
    }
    
    private void BuyPoints() {
        startActivity(new Intent(Transparent_wine_types.this, PurchasePacks.class));
    }
}
