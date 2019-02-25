package com.testlabic.datenearu.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Fragments.AllMessagesListFragment;
import com.testlabic.datenearu.Fragments.NotificationParent;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.NewUserSetupUtils.NewUserSetup;
import com.testlabic.datenearu.PaperOnboardingActivity;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.TransitionUtils.pagerTransition;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import com.testlabic.datenearu.Fragments.NotificationFragment;
import com.testlabic.datenearu.Fragments.ProfileFragment;
import com.testlabic.datenearu.Utils.locationUpdater;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private BottomBar bottomBar;
    private int count = 0;
    private int messagesUnread = 0;
    private int giftUnopened =0;
    FirebaseAuth.AuthStateListener authStateListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //initialize the startappSDK
        
        // StartAppSDK.init(this, "211455651", false);
        
        // StartAppAd.disableSplash();
        boolean moveToLocationActivity = getIntent().getBooleanExtra(Constants.moveToLocationActivity, false);
        if (moveToLocationActivity)
            startActivity(new Intent(this, locationUpdater.class));
        bottomBar = findViewById(R.id.bottomBar);
        mAuth = FirebaseAuth.getInstance();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        boolean refresh = getIntent().getBooleanExtra(Constants.refresh, false);
       
        /*
        on create switch to the home fragment
         */
        
        changeFragment(new pagerTransition());
        
        //setCustomFontAndStyle(tabLayout1, 0);
        
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                
                if (tabId == R.id.tab_message) {
                    // switch to messages fragment
                    changeFragment(new AllMessagesListFragment());
                    BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_message);
                    nearby.removeBadge();
                } else if (tabId == R.id.tab_home) {
                    // switch to messages fragment
                    changeFragment(new pagerTransition());
                } else if (tabId == R.id.tab_profile) {
                    // switch to messages fragment
                    changeFragment(new ProfileFragment());
                } else if (tabId == R.id.tab_notif) {
                    changeFragment(new NotificationParent());
                    BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_notif);
                    nearby.removeBadge();
                }
                
            }
        });
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        boolean trail = sharedPref.getBoolean("trialover", true);
        if (trail) {
            blurtrial();
        }
        
    }
    
    private void blurtrial() {
        
        //TODO: check for blur condition if on then only display this message
        //code for blur trial
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userInfo).child(Constants.uid).child("creationTime").child(Constants.timeStamp);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        
                        if (dataSnapshot.getValue() != null) {
                            long timestamp = dataSnapshot.getValue(Long.class);
                            long epoch = System.currentTimeMillis() / 1000;
                            long oneday = 86400;
                            if ((epoch + (5 * oneday) >= timestamp) && ((epoch + (6 * oneday)) <= timestamp)) {
                                SweetAlertDialog alertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Trial period Over!!")
                                        .setContentText("Tomorrow your profile will be unblured")
                                        .setCancelText("Leave")
                                        .setConfirmButton("Buy Drops", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                startActivity(new Intent(MainActivity.this, PurchasePacks.class));
                                                
                                            }
                                        });
                                alertDialog.show();
                                Button btn = alertDialog.findViewById(R.id.confirm_button);
                                btn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                                btn1.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                
                            }
                            if ((epoch + (7 * oneday)) <= timestamp) {
                                final SweetAlertDialog alertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Trial period Over!!")
                                        .setContentText("If you press Cancel your profile will be unblured")
                                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.userInfo).child(Constants.uid);
                                                HashMap<String, Object> update_blur = new HashMap<>();
                                                update_blur.put("isBlur", false);
                                                ref1.updateChildren(update_blur);
                                                sweetAlertDialog.dismiss();
                                            }
                                        })
                                        .setConfirmButton("Buy Drops", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                startActivity(new Intent(MainActivity.this, PurchasePacks.class));
                                                
                                            }
                                        });
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                                Button btn = alertDialog.findViewById(R.id.confirm_button);
                                btn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                                btn1.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                
                                //code so that does not run again and again
                                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean("trialover", false);
                                editor.commit();
                            }
                            //Log.e(TAG, String.valueOf(timestamp));
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    
                    }
                });
            }
        }, 5000);
        
    }
    
    private void checkForNotification() {
        count = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(Constants.uid).child(Constants.unread);
        
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                count = (int) dataSnapshot.getChildrenCount();
                bottomBar = findViewById(R.id.bottomBar);
                BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_notif);
                if (count > 0) {
                    
                    nearby.setBadgeCount(count);
                    
                    // Remove the badge when you're done with it.
                } else
                    nearby.removeBadge();
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    public void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
    
    @Override
    protected void onResume() {
        Log.e(TAG, "Main Activity onresume called! " + Constants.uid);
        //Toast.makeText(this, "Main Activity onresume called! "+Constants.uid, Toast.LENGTH_SHORT ).show();
        super.onResume();
        Constants.uid = FirebaseAuth.getInstance().getUid();
        if (Constants.uid == null) {
            startActivity(new Intent(MainActivity.this, SignIn.class));
            finish();
            
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                
                Log.e(TAG, "Firebase auth changed!");
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, PaperOnboardingActivity.class));
                    finish();
                } else {
                    Constants.uid = firebaseAuth.getUid();
                    checkForNotification();
                    checkForNewMessages();
                    checkForNewGifts();
                    updateStatus(Constants.online);
                    checkForIncompleteData();
                }
                
            }
        };
        mAuth.addAuthStateListener(authStateListener);
        
    }
    
    private void checkForNewGifts() {
        giftUnopened =0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid).child(Constants.unread);
    
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            
                giftUnopened = (int) dataSnapshot.getChildrenCount();
                giftUnopened  += count;
                //Log.e(TAG, giftUnopened+" yyy");
                bottomBar = findViewById(R.id.bottomBar);
                BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_notif);
                if (giftUnopened > 0) {
                
                    nearby.setBadgeCount(giftUnopened);
                
                    // Remove the badge when you're done with it.
                } else
                    nearby.removeBadge();
            
            }
        
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void checkForIncompleteData() {
        
        //SharedPreferences sharedPreferences = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
        //boolean checkRecovery = sharedPreferences.getBoolean(Constants.newUserSetupDone, false);
        //if (checkRecovery)
        {
            DatabaseReference recoveryRef = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(Constants.uid);
            
            recoveryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    // Log.e(TAG, "Fix method called for recovery");
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null) {
                        if (user.getUserName() == null || user.getInterestedIn() == null || user.getGender() == null || user.getNumeralAge() < 0 || user.getMatchAlgo() == null) {
                            //reRun Activity to fill info!
                            Toast.makeText(MainActivity.this, "Please fill the details to continue!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, NewUserSetup.class));
                        } else if (user.getCityLabel() == null) {
                            Toast.makeText(MainActivity.this, "Please fill the city or your location to continue!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, locationUpdater.class));
                        }
                   /* if(!user.isQuestionaireComplete())
                    {
                        Toast.makeText(MainActivity.this, "Please fill the answers to continue!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, QuestionSetup.class));
                    }*/
                    }
                    
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        }
    }
    
    private void checkForNewMessages() {
        messagesUnread = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS + Constants.unread)
                .child(Constants.uid + Constants.unread);
        
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesUnread = (int) dataSnapshot.getChildrenCount();
                BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_message);
                if (messagesUnread > 0) {
                    
                    nearby.setBadgeCount(messagesUnread);
                    // Remove the badge when you're done with it.
                    
                } else
                    nearby.removeBadge();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // startActivity(new Intent(MainActivity.this, MainActivity.class));
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        updateStatus(Constants.offline);
        if (mAuth != null && authStateListener != null)
            mAuth.removeAuthStateListener(authStateListener);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // updateStatus(Constants.offline);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.uid != null)
            updateStatus(Constants.offline);
    }
    
    private void updateStatus(final String status) {
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put(Constants.status, status);
        if (Constants.uid == null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.usersStatus)
                .child(Constants.uid);
        reference.updateChildren(updateStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (status.equals(Constants.online)) {
                
                }
            }
        });
        
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
        /*
        move to home fragment if on any other fragment, else exit
         */
        
    }
    
    public void giveXPoints() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        HashMap<String, Object> updatePoints = new HashMap<>();
        updatePoints.put(Constants.xPoints, 1000);
        reference.updateChildren(updatePoints);
        
    }
}










