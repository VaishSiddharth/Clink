package com.testlabic.datenearu.ProfileUtils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mbieniek.facebookimagepicker.facebook.FacebookAlbumPickerActivity;
import com.testlabic.datenearu.R;

import static com.mbieniek.facebookimagepicker.facebook.FacebookAlbumPickerActivityKt.FACEBOOK_IMAGE_ACTIVITY_REQUEST_CODE;

public class UploadPhotos extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = findViewById(R.id.upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadPhotos.this, FacebookAlbumPickerActivity.class);
                startActivityForResult(intent, FACEBOOK_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }
}
