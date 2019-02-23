package com.testlabic.datenearu.AttemptMatchUtils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

public class AttemptQuestionsStepperAdapter extends AbstractFragmentStepAdapter {
    public AttemptQuestionsStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        if(position==0) {
            return new AttemptQuestionOne();
        }
        else
        if(position==1)
            return new AttemptQuestionTwo();
        else
        if(position==2)
            return new AttemptQuestionThree();
        else
        if(position==3)
            return new AttemptQuestionFour();
        else
        if(position==4)
            return new AttemptQuestionFive();
        else
            if(position==5)
                return new AttemptQuestionSix();
        else
        if(position==6)
            return new AttemptQuestionSeven();
        else
        if(position==7)
            return new AttemptQuestionEight();
        else
        if(position==8)
            return new AttemptQuestionNine();
        else
        if(position==9)
            return new AttemptQuestionTen();

        //  Bundle b = new Bundle();
        //  b.putInt(CURRENT_STEP_POSITION_KEY, position);
        //  step.setArguments(b);
        return new AttemptQuestionOne();
    }

    @Override
    public int getCount() {
        return 10;
    }
}
