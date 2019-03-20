package com.testlabic.datenearu.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.PaperOnboardingActivity;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.WaveDrawable;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    ImageView applogo,imageView;
    ImageView line1;
    Animation uptodown,downtoup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //line1 =findViewById(R.id.line1);
        applogo =findViewById(R.id.applogo);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);

        imageView=findViewById(R.id.test);
        //applogo = findViewById(R.id.applogo);
        WaveDrawable mWaveDrawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mWaveDrawable = new WaveDrawable(getDrawable(R.drawable.nohere));
        }
        if (mWaveDrawable != null) {
            mWaveDrawable.setWaveAmplitude(30);
            mWaveDrawable.setWaveLength(580);
            mWaveDrawable.setWaveSpeed(12);
            mWaveDrawable.setIndeterminate(true);
        }

        //((WaveDrawable) mWaveDrawable).setLevel(20);

        imageView.setImageDrawable(mWaveDrawable);
        //line1.setAnimation(uptodown);
        //line3.setAnimation(downtoup);
        AlphaAnimation anim1 = new AlphaAnimation(0.0f, 1.0f);
        anim1.setStartOffset(200);
        anim1.setDuration(1000);
        //anim1.setRepeatCount(10);
        //anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
        applogo.startAnimation(anim1);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Create an Intent that will start the MainActivity.
                
            }
        }, SPLASH_DISPLAY_LENGTH);
        setUpConnectionTest();
    }
    
    private void setUpConnectionTest() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("test");
        reference.setValue("Test");
        
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.e("App: ", "connected");
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    SplashScreen.this.finish();
                } else {
                    Log.e("App: ", "not connected");
                    Toast.makeText(SplashScreen.this, "Trouble connecting? use WIFI or VPN", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                //System.err.println("Listener was cancelled");
                Log.e("App: ", "On cancelled called!");
            }
        });
    }
    
}
