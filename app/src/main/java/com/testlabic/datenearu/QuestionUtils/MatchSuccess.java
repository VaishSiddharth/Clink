package com.testlabic.datenearu.QuestionUtils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class MatchSuccess extends AppCompatActivity {
    
    
    String clickedUsersId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_success);
        clickedUsersId = getIntent().getStringExtra(Constants.clickedUid);
        SendNotification();
    }
    
    private void SendNotification() {
    }
}
