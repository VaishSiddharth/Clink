package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;
import com.testlabic.datenearu.R;

public class MyStepperAdapter extends AbstractFragmentStepAdapter {
    
    private static final String CURRENT_STEP_POSITION_KEY = "currentPosition";
    
    public MyStepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        if(position==0) {
            return new Name();
        }
        else
            if(position==1)
               return new Age();
        else
            if(position==2)
                return new Gender();
        else
            if(position==3)
                return new OneLineDesc();
        
      //  Bundle b = new Bundle();
      //  b.putInt(CURRENT_STEP_POSITION_KEY, position);
      //  step.setArguments(b);
        return new Age();
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        StepViewModel.Builder builder = new StepViewModel.Builder(context)
                .setTitle(R.string.tab_title);
        switch (position) {
            case 0:
                builder
                        .setEndButtonLabel("NEXT")
                        .setBackButtonLabel("BACK")
                        .setTitle("Name");
                break;
            case 1:
                builder
                        .setEndButtonLabel("NEXT")
                        .setBackButtonLabel("BACK")
                        .setTitle("Birthday");
                break;
            case 2:
                builder
                        .setEndButtonLabel("NEXT")
                        .setBackButtonLabel("BACK")
                        .setTitle("Gender");
                break;
            case 3:
                builder
                        .setEndButtonLabel("COMPLETE")
                        .setBackButtonLabel("BACK")
                        .setTitle("About you");
                break;
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
        return builder.create();
    }

    @Override
    public int getCount() {
        return 4;
    }
}