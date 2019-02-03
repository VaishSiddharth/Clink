package com.testlabic.datenearu.NewQuestionUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

public class QuestionsStepperAdapter extends AbstractFragmentStepAdapter {
    public QuestionsStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        if(position==0) {

            return new QuestionOne();
        }
        else
        if(position==1)
            return new QuestionTwo();
        else
        if(position==2)
            return new QuestionThree();
        else
        if(position==3)
            return new QuestionFour();
        else
        if(position==4)
            return new QuestionFive();
        else
            if(position==5)
                return new QuestionSix();
        else
        if(position==6)
            return new QuestionSeven();
        else
        if(position==7)
            return new QuestionEight();
        else
        if(position==8)
            return new QuestionNine();
        else
        if(position==9)
            return new QuestionTen();

        //  Bundle b = new Bundle();
        //  b.putInt(CURRENT_STEP_POSITION_KEY, position);
        //  step.setArguments(b);
        return new QuestionOne();
    }

    @Override
    public int getCount() {
        return 10;
    }
}
