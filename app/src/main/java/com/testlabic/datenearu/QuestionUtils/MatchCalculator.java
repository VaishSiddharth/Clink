package com.testlabic.datenearu.QuestionUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.Models.ModelMessage;
import com.testlabic.datenearu.Models.ModelNotification;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.sql.Date;

public class MatchCalculator extends AppCompatActivity {
    
    private static final String TAG = MatchCalculator.class.getSimpleName();
    private String clickedUsersId;
    private TextView statusMatch;
    private GoogleProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_calculator);
        statusMatch = findViewById(R.id.statusMatch);
        progressBar = findViewById(R.id.google_progress);
        final int score = getIntent().getIntExtra(Constants.score, 0);
        Log.e(TAG, "The score is " + String.valueOf(score));
        Toast.makeText(this, "The score is " + String.valueOf(score), Toast.LENGTH_SHORT).show();
        clickedUsersId = getIntent().getStringExtra(Constants.clickedUid);
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (score > 7) {
                    if (clickedUsersId != null) {
                        SendNotification();
                        //SendRequestMessage();
                    }
                    progressBar.setVisibility(View.GONE);
                    statusMatch.setText(getResources().getString(R.string.matchSuccess));
                } else {
                    progressBar.setVisibility(View.GONE);
                    statusMatch.setText(getResources().getString(R.string.matchFailed));
        
                }
            }
        }, 2500);
        
       
    }
    
    private void SendRequestMessage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(clickedUsersId).child(Constants.requestMessages)
                .child(Constants.uid);
    
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
    
            String photoUrl = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
            String name = String.valueOf(user.getDisplayName());
            long timeStamp = new java.util.Date().getTime();
            String txt = "Hey! We seem to have a lot of similarities, do you wish to talk?";
            ModelMessage message = new ModelMessage(photoUrl, name, timeStamp,txt );
            reference.setValue(message);
        }
    }
    
    private void SendNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(clickedUsersId).child(Constants.unread).push();
        /*
        constructing message!
         */
        
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String message = userName + " attempted match with you, and passed your test! \nConnect with him by accepting the request.";
        
        long timeStamp = new java.util.Date().getTime();
        String url = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        ModelNotification notification = new ModelNotification(message, Constants.uid, timeStamp,url );
        
        reference.setValue(notification);
    }
    
    @Override
    public void onBackPressed() {
        Intent i = new Intent(MatchCalculator.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
