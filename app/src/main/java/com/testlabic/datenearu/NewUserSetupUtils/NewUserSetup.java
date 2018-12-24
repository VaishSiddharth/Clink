package com.testlabic.datenearu.NewUserSetupUtils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.R;

public class NewUserSetup extends AppCompatActivity  implements StepperLayout.StepperListener{
    
    private StepperLayout mStepperLayout;
     MyStepperAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_setup);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        adapter = new MyStepperAdapter(getSupportFragmentManager(), this);
        adapter.createStep(0);
        adapter.createStep(1);
        adapter.createStep(2);
        adapter.createStep(3);
        mStepperLayout.setAdapter(adapter);
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
}
