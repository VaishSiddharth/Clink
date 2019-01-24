package com.testlabic.datenearu.NewQuestionUtils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.QuestionUtils.ModelQuestion;
import com.testlabic.datenearu.R;

import java.util.ArrayList;

public class QuestionSetup extends AppCompatActivity implements StepperLayout.StepperListener {

    private StepperLayout mStepperLayout;
    boolean proceedAhead;
    ArrayList<ModelQuestion> questions;
    QuestionsStepperAdapter questionsStepperAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_setup);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        //setUpStatusbarColor();
        questionsStepperAdapter = new QuestionsStepperAdapter(getSupportFragmentManager(), this);
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

    }

    @Override
    public void onCompleted(View completeButton) {
        if(proceedAhead)
            completeButton.setEnabled(true);
        else {
            completeButton.setEnabled(false);
            Toast.makeText(QuestionSetup.this, "Wait a while!", Toast.LENGTH_SHORT).show();
        }

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
}
