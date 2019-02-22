package com.testlabic.datenearu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.testlabic.datenearu.AttemptMatchUtils.QuestionsAttemptActivity;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelNotification;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.QuestionUtils.QuestionsActivity;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import com.testlabic.datenearu.Adapters.View_Pager_Adapter;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ClickedUser extends AppCompatActivity implements View.OnClickListener {
    
    private static final String TAG = ClickedUser.class.getSimpleName();
    ShineButton f1;
    ImageView backbutton;
    boolean first = true;
    String imageUrl, imageUrl2, imageUrl3;
    private TextView name, about, age;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    public View_Pager_Adapter view_pager_adapter;
    private String clickedUid;
    private TextView attemptMatch;
    private Boolean comingFromNotif = false;
    private Boolean isBlur = false;
    SweetAlertDialog alertDialog;
    DatabaseReference ref;
    ValueEventListener userInfoListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_user);
        
        f1 = findViewById(R.id.f1);
       // backbutton=findViewById(R.id.backbutton);
       /* backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        f1.setOnClickListener(this);
        clickedUid = getIntent().getStringExtra(Constants.clickedUid);
        comingFromNotif = getIntent().getBooleanExtra(Constants.comingFromNotif, false);
        isBlur = getIntent().getBooleanExtra(Constants.isBlur, false);
        
        if (clickedUid != null)
            setUpDetails();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleindicator);
        attemptMatch = findViewById(R.id.attempt_match);
        name = findViewById(R.id.name);
        about = findViewById(R.id.about);
        //
        
        if (!comingFromNotif) {
            attemptMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        } else {
            attemptMatch.setText("Accept!");
            attemptMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowAConfirmationDialog(clickedUid);
                }
            });
        }
    }
    
    private void ShowAConfirmationDialog(final String uid) {
      SweetAlertDialog alertDialog =  new SweetAlertDialog(ClickedUser.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You guys will be added as a connection to each other!")
                .setConfirmText("Yes, go for it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                        KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
                        konfettiView.build()
                                .addColors(Color.YELLOW, Color.GREEN, Color.BLACK, Color.MAGENTA)
                                .setDirection(0.0, 359.0)
                                .setSpeed(1f, 5f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(2000L)
                                .addShapes(Shape.RECT, Shape.CIRCLE)
                                .addSizes(new Size(12, 5))
                                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                                .streamFor(300, 5000L);
                        acceptRequest(uid, sDialog);
                    }
                })
              ;
      alertDialog.show();
    
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
    }
    
    private void acceptRequest(String item, final SweetAlertDialog sDialog) {
        if (item != null) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.Messages)
                    .child(Constants.uid)
                    .child(Constants.contacts)
                    .child(item);
            
            DatabaseReference receiver = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(item);
            receiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if (dataSnapshot.getValue() != null) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid(), user.getOneLine(), null);
                            ref.setValue(contact);
                        }
                    }
                    
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
            
            /*
            
            Similarly setup contact for the other user
             */
            
            final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.Messages)
                    .child(item)
                    .child(Constants.contacts)
                    .child(Constants.uid);
            
            DatabaseReference receiver2 = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(Constants.uid);
            receiver2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if (dataSnapshot.getValue() != null) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid(), user.getOneLine(), null);
                            ref2.setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sDialog
                                            .setTitleText("Success!")
                                            .setContentText("Go have a talk, get happy!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    
                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1500);
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
    }
    
    private void showDialog() {
        
        alertDialog = new SweetAlertDialog(this)
                .setTitleText("Attempt match?")
                .setContentText("You will have to answer ten questions, and if you win you get a chance to connect, it will cost you 100 points continue")
                .setConfirmText("Yes!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.setContentText("Just a while, stay here!");
                        sDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                        final DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.xPoints)
                                .child(Constants.uid);
                        
                        xPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                
                                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                                if (modelSubscr != null) {
                                    int current = modelSubscr.getXPoints();
                                    if (current < Constants.attemptTestPoints) {
                                        Toast.makeText(ClickedUser.this, "You don't have enough drops, buy now!", Toast.LENGTH_SHORT).show();
                                        Handler h = new Handler();
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                BuyPoints();
                                            }
                                        }, 1000);
                                       
                                    } else {
                                        current -= Constants.attemptTestPoints;
                                        HashMap<String, Object> updatePoints = new HashMap<>();
                                        updatePoints.put(Constants.xPoints, current);
                                        dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sDialog
                                                        .setTitleText("Starting!")
                                                        .setContentText("Best of luck!")
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(new Intent(ClickedUser.this, QuestionsAttemptActivity.class).putExtra(Constants.clickedUid, clickedUid));
                                                        sDialog.dismiss();
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
                        
                        ///
                    }
                });
        alertDialog.show();
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
        Button btn1 = alertDialog.findViewById(R.id.cancel_button);
        btn1.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
        
    }
    
    private void BuyPoints() {
        alertDialog.dismiss();
        startActivity(new Intent(ClickedUser.this, PurchasePacks.class));
    }
    
    private void setUpDetails() {
        
        ref = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo).child(clickedUid);
        
        userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null && user.getUserName() != null)
                        name.setText(user.getUserName());
                    
                    if (user != null && user.getAbout() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            about.setText(Html.fromHtml(user.getAbout(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            about.setText(Html.fromHtml(user.getAbout()));
                        }
                    }
                    //blur image and name if isBlur
                    if (user != null) {
                        if (user.getIsBlur()) {
                            name.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            float radius = name.getTextSize() / 3;
                            BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                            name.getPaint().setMaskFilter(filter);
                        }
                        else
                        {
                            //unblur code
                            
                        }
                        
                    }
                    
                    //setupImages now
                    if (user != null) {
                        imageUrl = user.getImageUrl();
                        imageUrl2 = user.getImage2();
                        imageUrl3 = user.getImage3();
                        //Log.e(TAG, imageUrl+ " "+ imageUrl2);
                        view_pager_adapter = new View_Pager_Adapter(getSupportFragmentManager(), imageUrl, imageUrl2, imageUrl3, isBlur);
                        viewPager.setAdapter(view_pager_adapter);
                        circleIndicator.setViewPager(viewPager);
                        view_pager_adapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
                    }
                    
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        };
        ref.addValueEventListener(userInfoListener);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUp();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    private void cleanUp()
    {
        if(ref!=null&&userInfoListener!=null)
            ref.removeEventListener(userInfoListener);
    }
    
    @Override
    public void onClick(View view) {
        if (first) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ClickedUser.this);
    
            boolean isFirstTime = sharedPreferences.getBoolean("firstLikeInfo", true);
            if (isFirstTime) {
                SweetAlertDialog alertDialog = new SweetAlertDialog(ClickedUser.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("How it works?")
                        .setContentText("When you like someone, we don't tell them that, but if they like you back, it'll be a match!")
                        .setConfirmButton("ok, like!", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("firstLikeInfo", false).apply();
                                executeLikeFunction();
                            }
                        });
                alertDialog.show();
                Button btn = alertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
            } else {
                executeLikeFunction();
                Toast.makeText(ClickedUser.this, "Liked!", Toast.LENGTH_SHORT).show();
            }
            first = false;
        } else {
            first = true;
        }
    }
    
    private void executeLikeFunction() {
        //give 10 drops for liking
        
        //check if the other person has already liked you!
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.LikeInfo)
                .child(Constants.uid)
                .child(clickedUid);
        checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //other person likes you; show match message!
                    //first check whether the contact is blocked or not
                    DatabaseReference blockRef = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.blockList)
                            .child(clickedUid)
                            .child(Constants.uid);
                    blockRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
                                    konfettiView.build()
                                            .addColors(Color.BLUE, Color.GREEN, Color.MAGENTA)
                                            .setDirection(0.0, 359.0)
                                            .setSpeed(1f, 5f)
                                            .setFadeOutEnabled(true)
                                            .setTimeToLive(2000L)
                                            .addShapes(Shape.RECT, Shape.CIRCLE)
                                            .addSizes(new Size(12, 5))
                                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                                            .streamFor(300, 5000L);
                                }
                                
                                SweetAlertDialog alertDialog = new SweetAlertDialog(ClickedUser.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setTitleText("Confirm Cheers?")
                                        .setContentText("Congrats, S/he already likes you, its a cheers!")
                                        .setConfirmButton("Sure", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.LikeInfo)
                                                        .child(clickedUid)
                                                        .child(Constants.uid);
                                                String index = "0";
                                                databaseReference.setValue(index);
                                                sendNotificationToOtherUser(sweetAlertDialog);
                                            }
                                        })
                                        .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                Button btn = alertDialog.findViewById(R.id.confirm_button);
                                btn.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
                                Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                                btn1.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
                            } else
                                Toast.makeText(ClickedUser.this, "Sorry an error occured", Toast.LENGTH_SHORT).show();
                        }
                        
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        
                        }
                    });
                    
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.LikeInfo)
                            .child(clickedUid)
                            .child(Constants.uid);
                    String index = "0";
                    databaseReference.setValue(index);
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
    }
    private void sendNotificationToOtherUser(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismissWithAnimation();
        acceptRequest(clickedUid, sweetAlertDialog);
        
        //create a notification
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(clickedUid).child(Constants.unread).push();
        //constructing message
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String message = userName + " liked you back! Go say hi now from messages screen.";
        
        long timeStamp = -1 * new java.util.Date().getTime();
        String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        ModelNotification notification = new ModelNotification(message, Constants.uid, timeStamp, url, false);
        
        reference.setValue(notification);
        
    }
    
}