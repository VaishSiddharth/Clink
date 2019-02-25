package com.testlabic.datenearu.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cooltechworks.views.ScratchImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelNotification;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;

import java.util.HashMap;

public class TransparentActivity extends Activity {
    
    String sendersUid;
    String nameS;
    KonfettiView viewKonfetti;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        boolean showDialog = getIntent().getBooleanExtra(Constants.showDialog, false);
        nameS = getIntent().getStringExtra(Constants.sendToName);
        sendersUid = getIntent().getStringExtra(Constants.clickedUid);
        if(showDialog)
            showSweetAlertDialog(sendersUid);
        
        AlphaAnimation anim1 = new AlphaAnimation(1.0f, 0.0f);
        anim1.setStartOffset(100);
        anim1.setDuration(1000);
        anim1.setRepeatCount(10);
        anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
      
        AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
        anim2.setStartOffset(100);
        anim2.setDuration(3000);
        anim1.setRepeatCount(10);
        anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
      
    
        //scratchImageView.setVisibility(View.GONE);
        viewKonfetti = findViewById(R.id.viewKonfetti);
        viewKonfetti.build()
                .addColors(getApplicationContext().getResources().getColor(R.color.appcolor),
                        getApplicationContext().getResources().getColor(R.color.yellow),
                        getApplicationContext().getResources().getColor(R.color.appcolor))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-100f, 1000f, -50f, -50f)
                .streamFor(500, 5000L);
        
    }
    
    private void showSweetAlertDialog(final String sendersUid) {
        SweetAlertDialog alertDialog = new SweetAlertDialog(TransparentActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Confirm Cheers?")
                .setContentText("Congrats, S/he already likes you, its a cheers!")
                .setConfirmButton("Sure", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.LikeInfo)
                                .child(sendersUid)
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
                        viewKonfetti.reset();
                        finish();
                    }
                });
        alertDialog.show();
        //testing transparent
    
    
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(TransparentActivity.this, R.drawable.button_4_dialogue));
        Button btn1 = alertDialog.findViewById(R.id.cancel_button);
        btn1.setBackground(ContextCompat.getDrawable(TransparentActivity.this, R.drawable.button_4_dialogue));
    }
    
    private void sendNotificationToOtherUser(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismissWithAnimation();
        acceptRequest(sendersUid, sweetAlertDialog);
        
        //create a notification
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(sendersUid).child(Constants.unread).push();
        //constructing message
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String message = userName + " liked you back! Go say hi now from messages screen.";
        
        long timeStamp = -1 * new java.util.Date().getTime();
        String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        ModelNotification notification = new ModelNotification(message, Constants.uid, timeStamp, url, false);
        
        reference.setValue(notification);
        
    }
    private void acceptRequest(final String item, final SweetAlertDialog sDialog) {
        if (item != null) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.Messages)
                    .child(Constants.uid)
                    .child(Constants.contacts)
                    .child(item);
            
            final DatabaseReference receiver = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(item);
            receiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if (dataSnapshot.getValue() != null) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            HashMap<String, Object> timeStamp = new HashMap<>();
                            timeStamp.put(Constants.timeStamp, ServerValue.TIMESTAMP);
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid(), user.getOneLine(), null);
                            ref.setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //on successful addition of contact move this uid to dmList of current user to prevent another tries!
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.DMIds)
                                            .child(Constants.uid)
                                            .push();
                                    reference.setValue(item);
                                }
                            });
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
                            HashMap<String, Object> timeStamp = new HashMap<>();
                            timeStamp.put(Constants.timeStamp, ServerValue.TIMESTAMP);
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid(), user.getOneLine(), null);
                            ref2.setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sDialog
                                            .setTitleText("Success!")
                                            .setContentText("You are good, go say hi!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    
                                    moveToChatScreen();
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
    private void moveToChatScreen() {
        
        Intent i = new Intent(TransparentActivity.this, chatFullScreen.class);
        i.putExtra(Constants.sendToUid, sendersUid);
        i.putExtra(Constants.sendToName, nameS);
        i.putExtra(Constants.directConvo, true);
        viewKonfetti.reset();
        finish();
        startActivity(i);
        
    }
    
}
