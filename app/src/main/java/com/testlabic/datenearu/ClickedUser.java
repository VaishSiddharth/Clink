package com.testlabic.datenearu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
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
import com.testlabic.datenearu.NewUserSetupUtils.QuestionsEnteringNewUser;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import com.testlabic.datenearu.Adapters.View_Pager_Adapter;
import com.testlabic.datenearu.Utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static java.security.AccessController.getContext;

public class ClickedUser extends AppCompatActivity implements View.OnClickListener {
    
    private static final String TAG = ClickedUser.class.getSimpleName();
    ShineButton f1;
    ImageView backbutton;
    boolean first = true;
    String imageUrl,imageUrl1, imageUrl2, imageUrl3;
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
    ImageView male;
    ImageView female;
    
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
        male=findViewById(R.id.maleglass);
        female=findViewById(R.id.femaleglass);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        about = findViewById(R.id.about);
        //
        
        if (!comingFromNotif) {
            attemptMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = getSharedPreferences(Constants.userDetailsOff, Context.MODE_PRIVATE);
                    boolean isQuestionaireComplete = prefs.getBoolean(Constants.isQuestionaireComplete+Constants.uid, false);
                    if(isQuestionaireComplete)
                        showDialog();
                    else
                        showFillQuestionaireDialog();
                }
            });
        } else {
            //check if the user can accept the connection!
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.acceptHistory)
                    .child(Constants.uid)
                    .child(clickedUid)
                    ;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists())
                    {
                        //accept request now!
                        attemptMatch.setText("Accept!");
                        attemptMatch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ShowAConfirmationDialog(clickedUid);
                            }
                        });
                    }
                    else
                        attemptMatch.setVisibility(View.GONE);
                    
                }
    
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
        
                }
            });
           
        }
    }
    
    private void showFillQuestionaireDialog() {
        SweetAlertDialog alertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Before you continue")
                .setContentText("Fill your quiz before you attempt other people's") .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        startActivity(new Intent(ClickedUser.this, QuestionsEnteringNewUser.class).putExtra(Constants.setupQuestions, true));
                    }
                });
        
        alertDialog.show();
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(ClickedUser.this, R.drawable.button_4_dialogue));
        if (getContext() != null) {
            btn.setTypeface(Utils.SFPRoLight(this));
            TextView title = alertDialog.findViewById(R.id.title_text);
            if(title!=null)
                title.setTypeface(Utils.SFProRegular(this));
        
            TextView contentText = alertDialog.findViewById(R.id.content_text);
            if(contentText!=null)
                contentText.setTypeface(Utils.SFPRoLight(this));
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
                                .addColors(getApplicationContext().getResources().getColor(R.color.appcolor),
                                        getApplicationContext().getResources().getColor(R.color.yellow),
                                        getApplicationContext().getResources().getColor(R.color.appcolor))
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
    
         {
             btn.setTypeface(Utils.SFPRoLight(this));
            TextView title = alertDialog.findViewById(R.id.title_text);
            if(title!=null)
                title.setTypeface(Utils.SFProRegular(this));
        
            TextView contentText = alertDialog.findViewById(R.id.content_text);
            if(contentText!=null)
                contentText.setTypeface(Utils.SFPRoLight(this));
        }
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
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid(), false, null);
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
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid(), false, null);
                            ref2.setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sDialog
                                            .setTitleText("Success!")
                                            .setContentText("Go have a talk, get happy!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    // add the transaction to accept list
    
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.acceptHistory)
                                            .child(Constants.uid)
                                            .child(clickedUid)
                                            ;
                                    reference.setValue("0");
                                    
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
                .setTitleText("Attempt cheers?")
                .setContentText("You will have to answer some questions, and if you do that right, you get a chance to connect")
                .setConfirmText("100 drops")
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
                                                        startActivity(new Intent(ClickedUser.this,
                                                                QuestionsAttemptActivity.class).putExtra(Constants.clickedUid, clickedUid));
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
        {
            if(btn!=null)
                btn.setTypeface(Utils.SFPRoLight(this));
            TextView title = alertDialog.findViewById(R.id.title_text);
            if(title!=null)
                title.setTypeface(Utils.SFProRegular(this));
        
            TextView contentText = alertDialog.findViewById(R.id.content_text);
            if(contentText!=null)
                contentText.setTypeface(Utils.SFPRoLight(this));
        }
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
    
                    if (user != null) {
                        String ageS = user.getNumeralAge()+"";
                        age.setText(ageS);
                    }
    
                    if (user != null && user.getAbout() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            about.setText(Html.fromHtml(user.getAbout(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            about.setText(Html.fromHtml(user.getAbout()));
                        }
                    }
                    if(user!=null&&user.getGender()!=null)
                    {
                        String gender =user.getGender();
                        if(gender.equalsIgnoreCase("female")) {
                            Log.e(TAG,gender);
                            male.setVisibility(View.GONE);
                            female.setVisibility(View.VISIBLE);

                        }
                        if(gender.equalsIgnoreCase("male")){
                            Log.e(TAG,gender);
                            female.setVisibility(View.GONE);
                            male.setVisibility(View.VISIBLE);
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
                        imageUrl1=user.getImage1();
                        imageUrl = user.getImageUrl();
                        imageUrl2 = user.getImage2();
                        imageUrl3 = user.getImage3();
                        //Log.e(TAG, imageUrl+ " "+ imageUrl2);
                        isBlur = user.getIsBlur();
                        view_pager_adapter = new View_Pager_Adapter(getSupportFragmentManager(), imageUrl1, imageUrl2, imageUrl3, isBlur);
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
                {
                    if(btn!=null)
                        btn.setTypeface(Utils.SFPRoLight(this));
                    TextView title = alertDialog.findViewById(R.id.title_text);
                    if(title!=null)
                        title.setTypeface(Utils.SFProRegular(this));
        
                    TextView contentText = alertDialog.findViewById(R.id.content_text);
                    if(contentText!=null)
                        contentText.setTypeface(Utils.SFPRoLight(this));
                }
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
                                            .addColors(getApplicationContext().getResources().getColor(R.color.appcolor),
                                                    getApplicationContext().getResources().getColor(R.color.yellow),
                                                    getApplicationContext().getResources().getColor(R.color.appcolor))
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
                                                        .child(Constants.uid)
                                                        .child(clickedUid);
                                                //String index = "0";
                                                databaseReference.setValue(null);
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
                                {
                                   
                                    btn.setTypeface(Utils.SFPRoLight(ClickedUser.this));
                                    btn1.setTypeface(Utils.SFPRoLight(ClickedUser.this));
                                    TextView title = alertDialog.findViewById(R.id.title_text);
                                    if(title!=null)
                                        title.setTypeface(Utils.SFProRegular(ClickedUser.this));
        
                                    TextView contentText = alertDialog.findViewById(R.id.content_text);
                                    if(contentText!=null)
                                        contentText.setTypeface(Utils.SFPRoLight(ClickedUser.this));
                                }
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