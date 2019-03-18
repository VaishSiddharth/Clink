package com.testlabic.datenearu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class ProfileImageView extends Activity {
    ImageView imagepv;
    String imageUrl;
    LinearLayout linearLayout;
    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_view);
        imagepv = findViewById(R.id.imagepv);
        linearLayout=findViewById(R.id.linearLayoutpv);
        Intent intent=getIntent();
        a=intent.getStringExtra("image");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    if(a.equalsIgnoreCase("1"))
                        imageUrl=user.getImage1();
                    else if(a.equalsIgnoreCase("2"))
                        imageUrl=user.getImage2();
                    else if(a.equalsIgnoreCase("3"))
                        imageUrl=user.getImage3();
                    else
                        imageUrl=user.getImageUrl();
                    Glide.with(ProfileImageView.this).load(imageUrl).into(imagepv);
                    /*linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProfileImageView.super.onBackPressed();
                        }
                    });*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
