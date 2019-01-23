package com.testlabic.datenearu.Activities;

import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import jp.wasabeef.blurry.Blurry;

public class EditActivity extends AppCompatActivity {
    
    TextView name, age, about;
    ImageView image1, nameWrap;
    Switch blur;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        about = findViewById(R.id.about);
        image1 = findViewById(R.id.image1);
        blur = findViewById(R.id.blurProfile);
        nameWrap = findViewById(R.id.name_wrap);
        blur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    blurProfile();
                }
                else
                    unBlurProfile();
            }
        });
        setUpDetails();
    }
    
    private void unBlurProfile() {
    
        startActivity(new Intent(EditActivity.this, EditActivity.class));
        finish();
    }
    
    private void blurProfile() {
        Blurry.with(EditActivity.this).capture(image1).into(image1);
//        Blurry.with(EditActivity.this).capture(nameWrap).into(nameWrap);
    
        if (Build.VERSION.SDK_INT >= 11) {
            name.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        float radius = name.getTextSize() / 3;
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        name.getPaint().setMaskFilter(filter);
    }
    
    private void setUpDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(ModelUser.class) != null) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null && user.getUserName() != null) {
                        name.setText(user.getUserName());
                        age.setText(String.valueOf(user.getNumeralAge()));
                        Glide.with(EditActivity.this).load(user.getImageUrl()).into(image1);
                    }
                   
                    
    
                    if (user != null && user.getAbout() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            about.setText(Html.fromHtml(user.getAbout(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            about.setText(Html.fromHtml(user.getAbout()));
                        }
                    }
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
}
