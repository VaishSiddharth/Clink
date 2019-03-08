package com.testlabic.datenearu.Utils;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.R;

public class Transparent_likeback extends Activity {
    
    ModelGift modelGift;
    ImageView imagePerson;
    TextView namePerson;
    RelativeLayout completeScreen;
    boolean tapTwice = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_likeback);
        modelGift = (ModelGift) getIntent().getSerializableExtra(Constants.giftModel);
        final ImageView premium_bottle = findViewById(R.id.premium_bottle);
        completeScreen = findViewById(R.id.completeScreen);
        moveGiftToRead();
        completeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("Trans", "The value of tap twice is "+ tapTwice);
                if (tapTwice)
                    finish();
                else {
                    tapTwice = true;
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tapTwice = false;
                        }
                    }, 2000);
                }
            }
        });
        imagePerson = findViewById(R.id.imageperson);
        namePerson = findViewById(R.id.nameperson);
        
        String url = modelGift.getGiftSendersImageUrl();
        Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(imagePerson);
        String message = modelGift.getGiftSendersName() + " cheered back, woohoo! (Tap to send message) ";
        namePerson.setText(message);
        namePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Transparent_likeback.this,chatFullScreen.class);
                i.putExtra(Constants.sendToUid, modelGift.getGiftSendersUid());
                i.putExtra(Constants.sendToName, modelGift.getGiftSendersName());
                i.putExtra(Constants.directConvo, true);
                startActivity(i);
    
            }
        });
    
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
        }, 8000);
    }
    
    private void moveGiftToRead() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.LikeBacks)
                .child(Constants.uid)
                .child(modelGift.getGiftSendersUid())
                ;
        reference.setValue(null);
      
    }
}
