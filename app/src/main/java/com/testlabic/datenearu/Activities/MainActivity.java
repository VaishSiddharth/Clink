package com.testlabic.datenearu.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Fragments.AllMessagesListFragment;
import com.testlabic.datenearu.Fragments.NotificationParent;
import com.testlabic.datenearu.AppIntro.IntroActivity;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.NewUserSetupUtils.NewUserSetup;
import com.testlabic.datenearu.NewUserSetupUtils.QuestionsEnteringNewUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.TransitionUtils.pagerTransition;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.testlabic.datenearu.Fragments.ProfileFragment;
import com.testlabic.datenearu.Utils.Transparent_likeback;
import com.testlabic.datenearu.Utils.Utils;
import com.testlabic.datenearu.Utils.locationUpdater;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private BottomBar bottomBar;
    private int count = 0;
    private int messagesUnread = 0;
    private boolean shownGifts = false;
    private int giftUnopened = 0;
    FirebaseAuth.AuthStateListener authStateListener;
    private boolean shownLikeBacks = false;
    private static boolean manygiftsactivity = true;
    private static boolean shownUnblurOnce = false;
    int questionss;
    int verCode;

    @Override
    protected void onStop() {
        super.onStop();
        updateStatus(Constants.offline);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the startappSDK

        StartAppSDK.init(this, "211455651", false);
        StartAppAd.disableSplash();
        StartAppAd.disableAutoInterstitial();
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        int questions = sharedPref.getInt("isQuestionComplete", 0);
        //Log.e(TAG,"session number "+questions);
        /*SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("trialover", false);
        editor.commit();*/

        boolean moveToLocationActivity = getIntent().getBooleanExtra(Constants.moveToLocationActivity, false);
        if (moveToLocationActivity)
            startActivity(new Intent(this, locationUpdater.class));
        bottomBar = findViewById(R.id.bottomBar);
        mAuth = FirebaseAuth.getInstance();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
        //on create switch to the home fragment
        changeFragment(new pagerTransition(), true);

        //setCustomFontAndStyle(tabLayout1, 0);
        rating();
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
    
                if (tabId == R.id.tab_message) {
                    // switch to messages fragment
                    changeFragment(new AllMessagesListFragment(), false);
                    BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_message);
                    nearby.removeBadge();
                } else if (tabId == R.id.tab_home) {
                    // switch to messages fragment
                    changeFragment(new pagerTransition(), true);
                } else if (tabId == R.id.tab_profile) {
                    // switch to messages fragment
                    changeFragment(new ProfileFragment(), false);
                } else if (tabId == R.id.tab_notif) {
                    changeFragment(new NotificationParent(), false);
                    BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_notif);
                    nearby.removeBadge();
                }

            }
        });

    }

    public void rating() {
        //rating
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(7)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child("Feedbacks").child(Constants.uid);
                        final HashMap<String, Object> updatefeedback = new HashMap<>();
                        updatefeedback.put("review", feedback);
                        ref.updateChildren(updatefeedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "We'll try to improve. Thanks", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).build();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ratingDialog.show();
            }
        }, 1000);
    }

    private void blurtrial() {
        //TODO: check for blur condition if on then only display this message
        //code for blur trial
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userInfo).child(Constants.uid).child("blurStartTime").child(Constants.timeStamp);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue(Long.class) != null) {
                            long timestamp = dataSnapshot.getValue(Long.class);
                            long epoch = System.currentTimeMillis();
                            long oneday = 86400000;
                            //Log.e(TAG, "The epoch and timestamps are " + timestamp + " " + epoch);
                            if (epoch >= (timestamp + (6 * oneday)) && ((epoch <= timestamp + (7 * oneday)))) {
                                SweetAlertDialog alertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Reminder (for blurred profiles)")
                                        .setContentText("Tomorrow your profile will be un-blurred, to continue with blurred profile you'll have to spend 500 drops tomorrow ")
                                        .setCancelText("Okay")
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

                                {
                                    btn.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                    btn1.setTypeface(Utils.SFPRoLight(MainActivity.this));

                                    TextView title = alertDialog.findViewById(R.id.title_text);
                                    if (title != null)
                                        title.setTypeface(Utils.SFProRegular(MainActivity.this));

                                    TextView contentText = alertDialog.findViewById(R.id.content_text);
                                    if (contentText != null)
                                        contentText.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                }

                            }
                            if (epoch >= (timestamp + (7 * oneday))) {
                                final SweetAlertDialog alertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Trial period Over (for blurred profiles)")
                                        .setContentText("If you press Cancel your profile will be unblured to users when viewed, to extend the time by 7 days spend 500 drops")
                                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.userInfo).child(Constants.uid);
                                                HashMap<String, Object> update_blur = new HashMap<>();
                                                update_blur.put("isBlur", false);
                                                HashMap<String, Object> update_blur_trial_ended = new HashMap<>();
                                                update_blur_trial_ended.put("blurTrialEnded", true);
                                                ref1.updateChildren(update_blur);
                                                //String gender =
                                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.cityLabels).child("male").child(Constants.uid);
                                                ref2.updateChildren(update_blur);
                                                //also increase the time and set it to today!

                                                //update the blurStartTime
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.userInfo).child(Constants.uid).child("blurStartTime");
                                                HashMap<String, Object> updateMap = new HashMap<>();
                                                updateMap.put(Constants.timeStamp, null);
                                                ref.updateChildren(updateMap);

                                                ref1.updateChildren(update_blur_trial_ended).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sweetAlertDialog.dismiss();
                                                    }
                                                });

                                            }
                                        })
                                        .setConfirmButton("500 drops", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                deductDropsAndIncreaseBlurTime(sweetAlertDialog);

                                            }
                                        });
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                                Button btn = alertDialog.findViewById(R.id.confirm_button);
                                btn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                                btn1.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));

                                {
                                    btn.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                    btn1.setTypeface(Utils.SFPRoLight(MainActivity.this));

                                    TextView title = alertDialog.findViewById(R.id.title_text);
                                    if (title != null)
                                        title.setTypeface(Utils.SFProRegular(MainActivity.this));

                                    TextView contentText = alertDialog.findViewById(R.id.content_text);
                                    if (contentText != null)
                                        contentText.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                }

                                //code so that does not run again and again
                                /*SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean("trialover", false);
                                editor.commit();*/
                            }
                            //Log.e(TAG, String.valueOf(timestamp));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }, 3000);

    }

    private void deductDropsAndIncreaseBlurTime(final SweetAlertDialog sDialog) {

        DatabaseReference attemptRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);

        ValueEventListener attemptListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                if (modelSubscr != null) {
                    int current = modelSubscr.getXPoints();
                    if (current < Constants.unBlurForSevenDaysDrops) {
                        Toast.makeText(MainActivity.this, "You don't have enough points, buy now!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, PurchasePacks.class));

                    } else {
                        current -= Constants.unBlurForSevenDaysDrops;
                        HashMap<String, Object> updatePoints = new HashMap<>();
                        updatePoints.put(Constants.xPoints, current);
                        Log.v(TAG, "Updating the drops here");
                        dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sDialog
                                        .setTitleText("Increasing duration!")
                                        .setContentText("Your profile will remain unblurred for 7 days from today!")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //update the blurStartTime
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.userInfo).child(Constants.uid).child("blurStartTime");
                                        HashMap<String, Object> updateMap = new HashMap<>();
                                        updateMap.put(Constants.timeStamp, ServerValue.TIMESTAMP);
                                        ref.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sDialog.dismiss();
                                            }
                                        });

                                    }
                                }, 2500);

                                Button btn = sDialog.findViewById(R.id.confirm_button);
                                btn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                Button btn1 = sDialog.findViewById(R.id.cancel_button);
                                btn1.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));

                                btn.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                btn1.setTypeface(Utils.SFPRoLight(MainActivity.this));

                                TextView title = sDialog.findViewById(R.id.title_text);
                                if (title != null)
                                    title.setTypeface(Utils.SFProRegular(MainActivity.this));

                                TextView contentText = sDialog.findViewById(R.id.content_text);
                                if (contentText != null)
                                    contentText.setTypeface(Utils.SFPRoLight(MainActivity.this));

                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        attemptRef.addListenerForSingleValueEvent(attemptListener);

    }

    public void appUpdates() {
        //Log.e(TAG,"Version "+verCode);
        final String appPackageName = this.getPackageName();// getPackageName() from Context or Activity object
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionCode;
            String version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("appVersionAuth");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String versionCode = dataSnapshot.getValue(String.class);
                String verCodestr = String.valueOf(verCode);
                if (!verCodestr.equalsIgnoreCase(versionCode)) {
                    //check if update compulsory..
    
                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child(Constants.updateNecessary);
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null)
                            {
                                boolean isCompul = dataSnapshot.getValue(Boolean.class);
                                Log.e(TAG, "The is compul is "+ isCompul);
                                if(isCompul)
                                {
                                    SweetAlertDialog alertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("App needs Update !!")
                                            .setContentText("Some new features are added & bugs are fixed please update the app otherwise the app will crash frequently.\n" +
                                                    "If this doesn't work please uninstall and reinstall CLINK\n")
                                            .setConfirmButton("Update", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                    
                                                }
                                            });
                                    alertDialog.setCancelable(false);
                                    alertDialog.show();
                                    Button btn = alertDialog.findViewById(R.id.confirm_button);
                                    btn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
                                    Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                                    btn1.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_4_dialogue));
    
                                    {
                                        btn.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                        btn1.setTypeface(Utils.SFPRoLight(MainActivity.this));
        
                                        TextView title = alertDialog.findViewById(R.id.title_text);
                                        if (title != null)
                                            title.setTypeface(Utils.SFProRegular(MainActivity.this));
        
                                        TextView contentText = alertDialog.findViewById(R.id.content_text);
                                        if (contentText != null)
                                            contentText.setTypeface(Utils.SFPRoLight(MainActivity.this));
                                    }
                                }
                            }
                        }
    
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
        
                        }
                    });
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    private boolean currentFragPager = false;
    
    public void changeFragment(Fragment fragment, Boolean home) {
        if (home)
            currentFragPager = true;
        else
            currentFragPager = false;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
    @Override
    protected void onResume() {
        Log.e(TAG, "Main Activity onresume called! " + Constants.uid);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                appUpdates();
            }
        },1000);
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
                    startActivity(new Intent(MainActivity.this, IntroActivity.class));
                    finish();
                } else {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int questions = sharedPref.getInt("isQuestionComplete", 0);
                    questions = questions + 1;
                    editor.putInt("isQuestionComplete", questions);
                    //Log.e(TAG,"session number "+questions);
                    editor.apply();
                    Constants.uid = firebaseAuth.getUid();
                    checkForNotification();
                    checkForNewMessages();
                    checkForUpdatingChecks();
                    checkForLikeBacks();
                    updateStatus(Constants.online);
                    checkForIncompleteData();
                    checkForInApps();
                    checkForBlur();
                }

            }
        };
        mAuth.addAuthStateListener(authStateListener);

    }

    private void checkForBlur() {
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        boolean trail = sharedPref.getBoolean("trialover", true);
        if (!shownUnblurOnce) {
            blurtrial();
            shownUnblurOnce = true;
        }
    }

    private void checkForInApps() {
        final BillingClient billingClient = BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

            }
        }).build();

        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(@BillingClient.BillingResponse int responseCode,
                                                          List<Purchase> purchasesList) {
                        if (responseCode == BillingClient.BillingResponse.OK
                                && purchasesList != null) {
                            for (Purchase purchase : purchasesList) {
                                // Process the result.
                                final ConsumeResponseListener listener = new ConsumeResponseListener() {
                                    @Override
                                    public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String outToken) {
                                        if (responseCode == BillingClient.BillingResponse.OK) {
                                            // Handle the success of the consume operation.
                                            // For example, increase the number of coins inside the user's basket.
                                            Log.e(TAG, "Consumption code in mainActivity");
                                        }
                                    }
                                };
                                billingClient.consumeAsync(purchase.getPurchaseToken(), listener);
                            }
                        }
                    }
                });
    }

    private void checkForLikeBacks() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.LikeBacks)
                .child(Constants.uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (!shownLikeBacks)
                        checkForNewLikeBacks();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkForNewLikeBacks() {
        shownLikeBacks = true;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.LikeBacks)
                .child(Constants.uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                //initiate transparent activity and pass data
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelGift modelGift = snapshot.getValue(ModelGift.class);
                            startActivity(new Intent(MainActivity.this, Transparent_likeback.class)
                                    .putExtra(Constants.giftModel, modelGift));

                        }
                    }
                }, 2500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForUpdatingChecks() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid)
                .child(Constants.unread);
        Log.e(TAG, "Check for checking called!");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(ModelGift.class) != null) {
                    if (!shownGifts)
                        checkForNewGifts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForNewGifts() {
        shownGifts = true;
        giftUnopened = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid).child(Constants.unread);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                giftUnopened = (int) dataSnapshot.getChildrenCount();
                giftUnopened += count;
                //Log.e(TAG, giftUnopened+" yyy");
                bottomBar = findViewById(R.id.bottomBar);
                BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_notif);
                if (giftUnopened > 0) {

                    nearby.setBadgeCount(giftUnopened);
                    //initiate transparent activity and pass data
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dataSnapshot.getChildrenCount() == 1)
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ModelGift modelGift = snapshot.getValue(ModelGift.class);
                                    Log.e(TAG, "Started gift once " + dataSnapshot);
                                    startActivity(new Intent(MainActivity.this, Transparent_gift_Activity.class)
                                            .putExtra(Constants.giftModel, modelGift));

                                }
                            if (dataSnapshot.getChildrenCount() > 1 && manygiftsactivity) {
                                manygiftsactivity = false;
                                startActivity(new Intent(MainActivity.this, Transparent_many_gifts.class));

                            }
                        }
                    }, 2500);

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

                        //update all sharedPref

                        if (user.getGender() != null) {
                            SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Constants.userGender, user.getGender()).apply();
                            editor.apply();
                        }

                        if (user.getInterestedIn() != null) {
                            SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Constants.userIntrGender, user.getGender()).apply();
                            editor.apply();
                        }

                        if (user.getUserName() == null || user.getInterestedIn() == null || user.getGender() == null || user.getNumeralAge() < 0 || user.getUserName().equals("")) {
                            //reRun Activity to fill info!
                            Toast.makeText(MainActivity.this, "Please fill the details to continue!", Toast.LENGTH_SHORT).show();
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this, NewUserSetup.class));

                                }
                            }, 350);
                        }
                        if (!user.isQuestionaireComplete()) {

                            SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(Constants.isQuestionaireComplete + Constants.uid, false);
                            editor.apply();
                            BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_profile);
                            nearby.setBadgeCount(1);
                            SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPref.edit();
                            questionss = sharedPref.getInt("isQuestionComplete", 0);
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (questionss >= 4) {
                                        Log.e(TAG, "session no. " + questionss);
                                        startActivity(new Intent(MainActivity.this, QuestionsEnteringNewUser.class).putExtra(Constants.setupQuestions, true));
                                    }

                                }
                            }, 1500);
                        } else if (user.isQuestionaireComplete()) {
                            SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(Constants.isQuestionaireComplete + Constants.uid, true);
                            editor.apply();
                            BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_profile);
                            nearby.removeBadge();

                        }
                        if (user.getAbout() == null) {
                            // Toast.makeText(MainActivity.this, "Please fill the city or your location to continue!", Toast.LENGTH_SHORT).show();
                            BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_profile);
                            nearby.setBadgeCount(1);
                            SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(Constants.isAboutComplete + Constants.uid, false);
                            editor.apply();
                        } else if (user.getAbout() != null) {
                            SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(Constants.isAboutComplete + Constants.uid, true);
                            editor.apply();
                        } else {
                            BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_profile);
                            nearby.removeBadge();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkForNewMessages() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS + Constants.unread)
                .child(Constants.uid + Constants.unread);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messagesUnread = 0;
                    messagesUnread = (int) dataSnapshot.getChildrenCount();
                    BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_message);
                    if (messagesUnread > 0) {
                        nearby.setBadgeCount(messagesUnread);
                        // Remove the badge when you're done with it.

                    } else
                        nearby.removeBadge();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        shownLikeBacks = false;

        shownGifts = false;
        updateStatus(Constants.offline);
        if (mAuth != null && authStateListener != null)
            mAuth.removeAuthStateListener(authStateListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.uid != null)
            updateStatus(null);

    }
    
    private boolean doubleBackToExitPressedOnce = false;
    
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        } else if (!currentFragPager) {
            changeFragment(new pagerTransition(), true);
            //set bottom bar to pagertransition
            bottomBar.selectTabAtPosition(0);
        }
        
        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateStatus(final String status) {
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put(Constants.status, status);

        final HashMap<String, Object> offlineTime = new HashMap<>();
        offlineTime.put(Constants.timeStamp, ServerValue.TIMESTAMP);

        if (Constants.uid == null)
            return;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.usersStatus)
                .child(Constants.uid);
        reference.updateChildren(updateStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (status == null) {
                    reference.updateChildren(offlineTime);
                }
            }
        });

    }

}










