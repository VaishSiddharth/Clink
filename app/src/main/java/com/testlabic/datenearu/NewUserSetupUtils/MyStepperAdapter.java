package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

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
                return new AboutYouEditor();
        
      //  Bundle b = new Bundle();
      //  b.putInt(CURRENT_STEP_POSITION_KEY, position);
      //  step.setArguments(b);
        return new Age();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        return new StepViewModel.Builder(context)
                .setTitle(R.string.tab_title) //can be a CharSequence instead
                .create();
    }
}