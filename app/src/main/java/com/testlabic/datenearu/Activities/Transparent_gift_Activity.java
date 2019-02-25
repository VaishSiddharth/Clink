package com.testlabic.datenearu.Activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cooltechworks.views.ScratchImageView;
import com.jackpocket.scratchoff.ScratchoffController;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class Transparent_gift_Activity extends Activity {
    
    ModelGift modelGift;
    ImageView imagePerson;
    TextView namePerson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_gift_);
        modelGift = (ModelGift) getIntent().getSerializableExtra(Constants.giftModel);
        final ImageView premium_bottle = findViewById(R.id.premium_bottle);
        ImageView regular_bottle = findViewById(R.id.royal_bottle);
        ImageView royal_bottle = findViewById(R.id.regular_bottle);
        //scratchImageView=findViewById(R.id.sample_image);
        //scratchImageView = new ScratchImageView(this);
        imagePerson = findViewById(R.id.imageperson);
        namePerson = findViewById(R.id.nameperson);
    
        String url = modelGift.getGiftSendersImageUrl();
        Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(imagePerson);
        String message = modelGift.getGiftSendersName() + " has sent you a gift (Tap to see Profile) ";
        namePerson.setText(message);
        namePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Transparent_gift_Activity.this, ClickedUser.class);
                i.putExtra(Constants.comingFromNotif, true);
                i.putExtra(Constants.clickedUid,modelGift.getGiftSendersImageUrl());
                finish();
                startActivity(i);
            }
        });
        
        ScratchoffController controller = new ScratchoffController(this)
                .setThresholdPercent(0.10d)
                .setTouchRadiusDip(this, 30)
                .setFadeOnClear(true)
                .setClearOnThresholdReached(true)
                .setCompletionCallback(new Runnable() {
                    @Override
                    public void run() {
                        premium_bottle.setVisibility(View.VISIBLE);
                        AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
                        anim2.setStartOffset(100);
                        anim2.setDuration(1000);
                        //anim1.setRepeatCount(10);
                        //anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
                        premium_bottle.startAnimation(anim2);
                        KonfettiView viewKonfetti = findViewById(R.id.viewKonfetti);
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
                                .streamFor(300, 5000L);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 10000);
                    }
                })
                .attach(findViewById(R.id.scratch_view), findViewById(R.id.scratch_view_behind));
    
    }
}
