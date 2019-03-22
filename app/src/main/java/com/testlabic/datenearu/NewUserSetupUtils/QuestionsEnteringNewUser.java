package com.testlabic.datenearu.NewUserSetupUtils;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.NewQuestionUtils.QuestionsStepperAdapter;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuestionsEnteringNewUser extends AppCompatActivity implements StepperLayout.StepperListener {
    
    private StepperLayout mStepperLayout;
    QuestionsStepperAdapter questionsStepperAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_setup);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        showInstructionDialog();
        questionsStepperAdapter = new QuestionsStepperAdapter(getSupportFragmentManager(), this);
        questionsStepperAdapter.createStep(0);
        questionsStepperAdapter.createStep(1);
        questionsStepperAdapter.createStep(2);
        questionsStepperAdapter.createStep(3);
        questionsStepperAdapter.createStep(4);
       // questionsStepperAdapter.createStep(5);
        //questionsStepperAdapter.createStep(6);
       // questionsStepperAdapter.createStep(7);
       // questionsStepperAdapter.createStep(8);
       // questionsStepperAdapter.createStep(9);
        mStepperLayout.setAdapter(questionsStepperAdapter);
        
    }
    
    @Override
    public void onCompleted(View completeButton) {
    
    }
    
    @Override
    public void onError(VerificationError verificationError) {
    
    }
    
    @Override
    public void onStepSelected(int newStepPosition) {
    
    }
    
    @Override
    public void onReturn() {
    
    }
    
    private void showInstructionDialog() {
        SweetAlertDialog alertDialog = new SweetAlertDialog(this)
                .setTitleText("Get Better Match!!")
                .setContentText("These Questions will be asked to everyone who tries to attempt match with you.\nFill them carefully. You can change the questions by tapping on the pen icon\n")
                .setConfirmText("Yes, go ahead!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
        alertDialog.show();
        
        Button btn = alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(QuestionsEnteringNewUser.this, R.drawable.button_4_dialogue));
        Button btn1 = alertDialog.findViewById(R.id.cancel_button);
        btn1.setBackground(ContextCompat.getDrawable(QuestionsEnteringNewUser.this, R.drawable.button_4_dialogue));
        {
            btn.setTypeface(Utils.SFPRoLight(this));
            btn1.setTypeface(Utils.SFPRoLight(this));
    
            TextView title = alertDialog.findViewById(R.id.title_text);
            if (title != null)
                title.setTypeface(Utils.SFProRegular(this));
            
            TextView contentText = alertDialog.findViewById(R.id.content_text);
            if (contentText != null)
                contentText.setTypeface(Utils.SFPRoLight(this));
        }
        
    }
}
