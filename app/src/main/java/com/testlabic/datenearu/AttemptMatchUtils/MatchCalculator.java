package com.testlabic.datenearu.AttemptMatchUtils;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelMessage;
import com.testlabic.datenearu.Models.ModelNotification;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import ru.github.igla.ferriswheel.FerrisWheelView;

public class MatchCalculator extends AppCompatActivity {
    
    private static final String TAG = MatchCalculator.class.getSimpleName();
    private String clickedUsersId;
    private FerrisWheelView ferrisWheelView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_calculator);
        TextView statusMatch = findViewById(R.id.statusMatch);
        ferrisWheelView = findViewById(R.id.google_progress);
        final int score = getIntent().getIntExtra(Constants.score, 0);
        Log.e(TAG, "The score is " + String.valueOf(score));
        Toast.makeText(this, "The score is " + String.valueOf(score), Toast.LENGTH_SHORT).show();
        clickedUsersId = getIntent().getStringExtra(Constants.clickedUid);
        ferrisWheelView.startAnimation();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (score > 7) {
                    if (clickedUsersId != null) {
                        showSuccessDialog();
                    }
                    
                    ferrisWheelView.stopAnimation();
                    ferrisWheelView.setVisibility(View.GONE);
                    //statusMatch.setText(getResources().getString(R.string.matchSuccess));
                } else {
                    ferrisWheelView.stopAnimation();
                    ferrisWheelView.setVisibility(View.GONE);
                    showFailedDialog();
                    // statusMatch.setText(getResources().getString(R.string.matchFailed));
                    
                }
            }
        }, 3000);
        
    }
    
    private void showFailedDialog() {
        
        // LayoutInflater factory = LayoutInflater.from(this);
        // final View filterDialogView = factory.inflate(R.layout.send_wine, null);
        
        SweetAlertDialog sweetAlertDialog1=new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog1.setTitleText("Oops...")
                .setContentText("Too different answers!\nHmmm, you can send a bottle of wine, s/he may notice you!\n(500 drops)")
                .setCustomImage(R.drawable.wine_bottle)
                .setConfirmButton("Yes!", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                        
                        //complete the transaction and update accordingly
                        
                        final DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.xPoints)
                                .child(Constants.uid);
                        
                        xPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                
                                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                                if (modelSubscr != null) {
                                    int current = modelSubscr.getXPoints();
                                    if (current < 500) {
                                        Toast.makeText(MatchCalculator.this, "You don't have enough drops, buy now!", Toast.LENGTH_SHORT).show();
                                        BuyPoints();
                                    } else {
                                        current -= Constants.wineAmount;
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
                                                sendWine();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(new Intent(MatchCalculator.this,MainActivity.class)
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
                })
                .show();
        Button btn=sweetAlertDialog1.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(this,R.drawable.button_4_dialogue));
    }
    
    private void sendWine() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(clickedUsersId).child(Constants.unread).push();
        /*
        constructing message!
         */
        
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String message = userName + " sent you a premium bottle of wine! \nCheck out his/her profile and connect with him by accepting the request.";
        
        long timeStamp = -1 * new java.util.Date().getTime();
        String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        ModelNotification notification = new ModelNotification(message, Constants.uid, timeStamp, url, false);
        
        reference.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                current += 300;
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
        startActivity(new Intent(MatchCalculator.this, PurchasePacks.class));
    }
    
    private void showSuccessDialog() {
        
        KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
        
        final SweetAlertDialog mainDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        mainDialog.setTitleText("You passed!");
        mainDialog.setContentText("Do you want us to notify the other person, and then if he/she wants, will be able to connect with you!");
        mainDialog.setConfirmButton("Sure!", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.Notifications)
                        .child(clickedUsersId).child(Constants.unread).push();
                                  /*
                                      constructing message!
                                  */
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String message = userName + " attempted match with you, and passed your test! \nConnect with him by accepting the request.";
                
                long timeStamp = -1 * new java.util.Date().getTime();
                String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                ModelNotification notification = new ModelNotification(message, Constants.uid, timeStamp, url, false);
                
                reference.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        
                        sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(true);
                        //sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL);
                        sweetAlertDialog.showCancelButton(false);
                        sweetAlertDialog
                                .setTitleText("Done!")
                                .setContentText("We wish you hear back soon!")
                                .setCancelButton("Leave it", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent i = new Intent(MatchCalculator.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                })
                                .setConfirmButton("Send message?", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        
                                        final LayoutInflater factory = LayoutInflater.from(MatchCalculator.this);
                                        final View editTextDialog = factory.inflate(R.layout.dialog_edittext, null);
                                        final EditText editText = editTextDialog.findViewById(R.id.ediText);
                                        
                                        SweetAlertDialog sweetAlertDialog1=new SweetAlertDialog(MatchCalculator.this, SweetAlertDialog.NORMAL_TYPE);
                                        // .setTitleText("Send a one-time message (25 pts)?")
                                        sweetAlertDialog1.setCustomView(editTextDialog);
                                        sweetAlertDialog1.setConfirmButton("Sure!", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(final SweetAlertDialog sDialog) {
                                                
                                                //display edit text
                                                //complete transaction
                                                
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
                                                            if (current < Constants.oneTimeMessageCost) {
                                                                Toast.makeText(MatchCalculator.this, "You don't have enough points, buy now!", Toast.LENGTH_SHORT).show();
                                                                BuyPoints();
                                                            } else {
                                                                current -= Constants.oneTimeMessageCost;
                                                                HashMap<String, Object> updatePoints = new HashMap<>();
                                                                updatePoints.put(Constants.xPoints, current);
                                                                dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        
                                                                        //first update notification
                                                                        
                                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                                                .child(Constants.Notifications)
                                                                                .child(clickedUsersId).child(Constants.unread).push();
                                                                        String messageText = editText.getText().toString();
                                                                        
                                                                        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                                                        String message = userName + " has sent you a one time message!\n\n" + "\"" + messageText + "\"";
                                                                        
                                                                        long timeStamp = -1 * new java.util.Date().getTime();
                                                                        String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                                                                        ModelNotification notification = new ModelNotification(message, Constants.uid, timeStamp, url, true);
                                                                        
                                                                        reference.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                
                                                                                sDialog
                                                                                        .setTitleText("Message Sent!")
                                                                                        .setContentText("Best of luck!")
                                                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                                                sDialog.findViewById(R.id.confirm_button).setVisibility(View.GONE);
                                                                                
                                                                                Handler handler = new Handler();
                                                                                handler.postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        startActivity(new Intent(MatchCalculator.this, MainActivity.class));
                                                                                        sDialog.dismiss();
                                                                                    }
                                                                                }, 2500);
                                                                            }
                                                                        });
                                                                        
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
                                        });
                                        
                                        sweetAlertDialog1.show();
                                        Button btn=sweetAlertDialog1.findViewById(R.id.confirm_button);
                                        btn.setBackground(ContextCompat.getDrawable(MatchCalculator.this,R.drawable.button_4_dialogue));
                                        Button btn1=sweetAlertDialog1.findViewById(R.id.cancel_button);
                                        btn1.setBackground(ContextCompat.getDrawable(MatchCalculator.this,R.drawable.button_4_dialogue));
                                    }
                                });
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        Button btn=sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackground(ContextCompat.getDrawable(MatchCalculator.this,R.drawable.button_4_dialogue));
                        Button btn1=sweetAlertDialog.findViewById(R.id.cancel_button);
                        btn1.setBackground(ContextCompat.getDrawable(MatchCalculator.this,R.drawable.button_4_dialogue));
                        
                        //show another dialog to write a one-time message!
                    }
                });
            }
        })
                .setCancelButton("No ", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SweetAlertDialog sweetAlertDialog1=new SweetAlertDialog(MatchCalculator.this, SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog1.setTitleText("Are you sure?");
                        sweetAlertDialog1.setContentText("You don't want him/her to know?");
                        sweetAlertDialog1.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                        sweetAlertDialog1.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent i = new Intent(MatchCalculator.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        });
                        sweetAlertDialog1.show();
                        Button btn=sweetAlertDialog1.findViewById(R.id.confirm_button);
                        btn.setBackground(ContextCompat.getDrawable(MatchCalculator.this,R.drawable.button_4_dialogue));
                        Button btn1=sweetAlertDialog1.findViewById(R.id.cancel_button);
                        btn1.setBackground(ContextCompat.getDrawable(MatchCalculator.this,R.drawable.button_4_dialogue));
                        
                    }
                });
        mainDialog.setCancelable(false);
        mainDialog.show();
        Button btn=mainDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(this,R.drawable.button_4_dialogue));
        Button btn1=mainDialog.findViewById(R.id.cancel_button);
        btn1.setBackground(ContextCompat.getDrawable(this,R.drawable.button_4_dialogue));
        
    }
    
    private void SendRequestMessage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(clickedUsersId).child(Constants.requestMessages)
                .child(Constants.uid);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            
            String photoUrl = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
            String name = String.valueOf(user.getDisplayName());
            long timeStamp = new java.util.Date().getTime();
            String txt = "Hey! We seem to have a lot of similarities, do you wish to talk?";
            ModelMessage message = new ModelMessage(photoUrl, name, timeStamp, txt);
            reference.setValue(message);
        }
    }
    
    
    @Override
    public void onBackPressed() {
        SweetAlertDialog alertDialog =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Quit?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        Intent i = new Intent(MatchCalculator.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
        alertDialog.show();
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(MatchCalculator.this, R.drawable.button_4_dialogue));
    }
}
