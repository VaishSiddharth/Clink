package com.testlabic.datenearu.QuestionUtils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class MatchCalculator extends AppCompatActivity {
    
    private static final String TAG = MatchCalculator.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_calculator);
        int score = getIntent().getIntExtra(Constants.score, 0);
        Log.e(TAG, "The score is "+ String.valueOf(score));
        Toast.makeText(this, "The score is "+ String.valueOf(score), Toast.LENGTH_SHORT).show();
    }
}
