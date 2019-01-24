package com.testlabic.datenearu.NewQuestionUtils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuestionOne extends Fragment implements BlockingStep {
    View rootView;
    ImageView next;
    SweetAlertDialog dialog;
    ImageView back;
    Button ans1;
    Button ans2;
    Button ans3;
    Button ans4;
    Boolean ansSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.activity_question_one, container, false);

        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");

        ans1=rootView.findViewById(R.id.ans1);
        ans2=rootView.findViewById(R.id.ans2);
        ans3=rootView.findViewById(R.id.ans3);
        ans4=rootView.findViewById(R.id.ans4);

        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans3);
                unColor(ans4);
                color(ans1);
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans1);
                unColor(ans3);
                unColor(ans4);
                color(ans2);
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans1);
                unColor(ans4);
                color(ans3);
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans3);
                unColor(ans1);
                color(ans4);
            }
        });


        return rootView;
    }

    private void unColor(Button button) {
        button.setBackground(getResources().getDrawable(R.drawable.border_black_box));
        button.setTextColor(getResources().getColor(R.color.light_black));
    }

    private void color (Button button)
    {
        button.setBackground(getResources().getDrawable(R.drawable.full_black_box));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (ansSelected) {
            dialog.show();
            callback.goToNextStep();
        }
        else {
            Toast.makeText(getActivity(), "Please Answer", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();

    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
