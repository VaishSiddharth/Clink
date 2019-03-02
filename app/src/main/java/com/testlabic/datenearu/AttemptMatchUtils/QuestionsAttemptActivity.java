package com.testlabic.datenearu.AttemptMatchUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.stepstone.stepper.StepperLayout;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuestionsAttemptActivity extends AppCompatActivity {
    private StepperLayout mStepperLayout;
    AttemptQuestionsStepperAdapter questionsStepperAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_attempt);
        
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        questionsStepperAdapter = new AttemptQuestionsStepperAdapter(getSupportFragmentManager(), this);
        questionsStepperAdapter.createStep(0);
        questionsStepperAdapter.createStep(1);
        questionsStepperAdapter.createStep(2);
        questionsStepperAdapter.createStep(3);
        questionsStepperAdapter.createStep(4);
        questionsStepperAdapter.createStep(5);
        questionsStepperAdapter.createStep(6);
        questionsStepperAdapter.createStep(7);
        questionsStepperAdapter.createStep(8);
        questionsStepperAdapter.createStep(9);
        mStepperLayout.setAdapter(questionsStepperAdapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getIntent().getStringExtra(Constants.clickedUid), 0).apply();
        
    }
    
    @Override
    public void onBackPressed() {
        showConfirmation();
    }
    
    private void showConfirmation() {
        SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Are you sure?")
                .setContentText("Quit?")
                .setConfirmText("Yes!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                })
                .show();
        Button btn=sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(this,R.drawable.button_4_dialogue));
        btn.setTypeface(Utils.SFPRoLight(this));
        TextView title = sweetAlertDialog.findViewById(R.id.title_text);
        if(title!=null)
            title.setTypeface(Utils.SFProRegular(this));
    
        TextView contentText = sweetAlertDialog.findViewById(R.id.content_text);
        if(contentText!=null)
            contentText.setTypeface(Utils.SFPRoLight(this));    }
    
}
