package com.testlabic.datenearu.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;

import android.app.Activity;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.TransitionUtils.CommonFragment;
import com.testlabic.datenearu.Utils.Constants;

public class ProfilePreview extends Activity {

    ImageView imagepv;
    TextView namepv, agepv, oneLinepv;
    ImageView femalepv, malepv;
    String imageUrl;
    boolean isBlur;
    View blur_viewpv;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_preview);
        linearLayout=findViewById(R.id.linearLayoutpv);
         AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
        anim2.setStartOffset(100);
        anim2.setDuration(1000);
        //anim1.setRepeatCount(10);
        //anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
        linearLayout.startAnimation(anim2);
        imagepv = findViewById(R.id.imagepv);
        namepv = findViewById(R.id.namepv);
        agepv = findViewById(R.id.agepv);
        oneLinepv = findViewById(R.id.oneLinepv);
        femalepv = findViewById(R.id.femaleglasspv);
        malepv = findViewById(R.id.maleglasspv);
        blur_viewpv=findViewById(R.id.view_on_blurpv);


        /*AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
        //anim2.setStartOffset(100);
        anim2.setDuration(2000);
        //anim1.setRepeatCount(10);
        //anim1.setRepeatMode(Animation.ZORDER_BOTTOM);
        linearLayout.startAnimation(anim2);*/

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {

                    namepv.setText(user.getUserName());
                    agepv.setText(user.getAge());
                    oneLinepv.setText(user.getOneLine());
                    imageUrl=user.getImageUrl();
                    isBlur=user.getIsBlur();

                    if (user.getGender() != null && user.getGender().equalsIgnoreCase("male")) {
                        malepv.setVisibility(View.VISIBLE);
                        femalepv.setVisibility(View.GONE);
                    }

                    if (user.getGender() != null && user.getGender().equalsIgnoreCase("female")) {
                        malepv.setVisibility(View.GONE);
                        femalepv.setVisibility(View.VISIBLE);
                    }

                    if(isBlur){
                        Glide.with(ProfilePreview.this).load(imageUrl).into(imagepv);
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blurData();
                            }
                        },1500);
                    }
                    else
                        Glide.with(ProfilePreview.this).load(imageUrl).into(imagepv);

                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProfilePreview.super.onBackPressed();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void blurData() {
        Blurry.with(imagepv.getContext()).radius(40)
                .sampling(2).capture(imagepv).into(imagepv);
        imagepv.setAlpha(0.7f);
        namepv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        float radius = namepv.getTextSize() / 3;
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        namepv.getPaint().setMaskFilter(filter);

        /*RequestOptions myOptions = new RequestOptions()
                .override(30, 30)
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        Glide.with(this)
                .load(imageUrl).apply(myOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Blurry.with(imagepv.getContext()).radius(20)
                                .sampling(2).capture(imagepv).into(imagepv);
                    }
                },10);
                return false;
            }
        }).into(imagepv);*/
        //imagepv.setAlpha(0.7f);
        blur_viewpv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
