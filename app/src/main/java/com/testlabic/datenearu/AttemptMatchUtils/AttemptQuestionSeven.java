package com.testlabic.datenearu.AttemptMatchUtils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.Models.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AttemptQuestionSeven extends Fragment implements BlockingStep {
    View rootView;
    SweetAlertDialog dialog;
    TextView question;
    Button ans1;
    Button ans2;
    Button ans3;
    Button ans4;
    Boolean ansSelected = false;
    String correctAnswer;
    String markedOption;
    DatabaseReference refInit;
    String clickedUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.activity_question_one, container, false);
        final int min = 0;
        final int max = 9;
        final int random = new Random().nextInt((max - min) + 1) + min;
        if(getActivity()!=null)
            clickedUid = getActivity().getIntent().getStringExtra(Constants.clickedUid);
        
        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");

        question=rootView.findViewById(R.id.question1);
        ans1=rootView.findViewById(R.id.ans1);
        ans2=rootView.findViewById(R.id.ans2);
        ans3=rootView.findViewById(R.id.ans3);
        ans4=rootView.findViewById(R.id.ans4);
        if(clickedUid!=null) {
            refInit = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(clickedUid)
                    .child("questions")
                    .child(Integer.toString(6));
            refInit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(ModelQuestion.class) != null) {
                        ModelQuestion modelQuestion = dataSnapshot.getValue(ModelQuestion.class);
                        if (modelQuestion != null) {
                            String  questionstr = modelQuestion.getQuestion();
                            String ans1str = modelQuestion.getOptA();
                            String ans2str = modelQuestion.getOptB();
                            String ans3str = modelQuestion.getOptC();
                            String ans4str = modelQuestion.getOptD();
                            correctAnswer = modelQuestion.getCorrectOption();
                            question.setText(questionstr);
                            ans1.setText(ans1str);
                            ans2.setText(ans2str);
                            ans3.setText(ans3str);
                            ans4.setText(ans4str);
                        }
                    }
                }
        
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
            
                }
            });
        }
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans3);
                unColor(ans4);
                color(ans1);
                ansSelected=true;
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans1);
                unColor(ans3);
                unColor(ans4);
                color(ans2);
                ansSelected=true;
    
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans1);
                unColor(ans4);
                color(ans3);
                ansSelected=true;
    
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans3);
                unColor(ans1);
                color(ans4);
                ansSelected=true;
    
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
        markedOption = button.getText().toString();
        button.setBackground(getResources().getDrawable(R.drawable.full_black_box));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
      
        if (ansSelected) {
            // show progress and update the question on the DB
            
            // increment the score for right answer in the sharedPref
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int score = sharedPreferences.getInt(clickedUid, 0);
            if(markedOption!=null&&markedOption.equals(correctAnswer))
            {
                score += 1;
                Editor editor = sharedPreferences.edit();
                editor.putInt(clickedUid, score).apply();
                callback.goToNextStep();
            } else
                callback.goToNextStep();
        }
        else {
            Toast.makeText(getActivity(), "Please Answer", Toast.LENGTH_SHORT).show();
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
