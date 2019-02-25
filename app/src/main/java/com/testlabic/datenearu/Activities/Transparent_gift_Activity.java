package com.testlabic.datenearu.Activities;

import androidx.appcompat.app.AppCompatActivity;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.cooltechworks.views.ScratchImageView;
import com.jackpocket.scratchoff.ScratchoffController;
import com.testlabic.datenearu.R;

public class Transparent_gift_Activity extends Activity {

    ScratchImageView scratchImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_gift_);

        final ImageView premium_bottle=findViewById(R.id.premium_bottle);
        ImageView regular_bottle=findViewById(R.id.royal_bottle);
        ImageView royal_bottle=findViewById(R.id.regular_bottle);
        //scratchImageView=findViewById(R.id.sample_image);
        //scratchImageView = new ScratchImageView(this);


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
                        KonfettiView viewKonfetti=findViewById(R.id.viewKonfetti);
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
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },10000);
                    }
                })
                .attach(findViewById(R.id.scratch_view), findViewById(R.id.scratch_view_behind));




        /*scratchImageView.setRevealListener(new ScratchImageView.IRevealListener() {
            @Override
            public void onRevealed(ScratchImageView tv) {
                // on reveal
                AlphaAnimation anim1 = new AlphaAnimation(1.0f, 0.0f);
                anim1.setStartOffset(100);
                anim1.setDuration(1000);
                //anim1.setRepeatCount(10);
                //anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
                scratchImageView.startAnimation(anim1);
                scratchImageView.setVisibility(View.GONE);
                premium_bottle.setVisibility(View.VISIBLE);
                AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
                anim2.setStartOffset(100);
                anim2.setDuration(3000);
                //anim1.setRepeatCount(10);
                //anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
                premium_bottle.startAnimation(anim2);

                //scratchImageView.setVisibility(View.GONE);
                KonfettiView viewKonfetti=findViewById(R.id.viewKonfetti);
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
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },10000);

            }

            @Override
            public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
                // on image percent reveal
                if (percent>0.5){
                    Toast.makeText(Transparent_gift_Activity.this,"Revealed",Toast.LENGTH_LONG).show();
                    scratchImageView.reveal();
                }
                else{
                    Toast.makeText(Transparent_gift_Activity.this,"Not Revealed",Toast.LENGTH_LONG).show();
                    //scratchImageView.reveal();
                }


            }
        });*/

    }
}
