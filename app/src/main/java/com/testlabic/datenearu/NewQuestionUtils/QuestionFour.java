package com.testlabic.datenearu.NewQuestionUtils;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuestionFour extends Fragment implements BlockingStep {
    View rootView;
    ImageView next;
    SweetAlertDialog dialog;
    ImageView back;
    TextView question;
    Button ans1;
    Button ans2;
    Button ans3;
    Button ans4;
    Boolean ansSelected = false;
    HashMap<String, Object> correctOption;
    DatabaseReference refInit;
    private boolean skipSelection = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.activity_question_one_editable, container, false);
        final int min = 0;
        final int max = 9;
        final int random = new Random().nextInt((max - min) + 1) + min;
        
        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");
        
        question=rootView.findViewById(R.id.question1);
        ans1=rootView.findViewById(R.id.ans1);
        ans2=rootView.findViewById(R.id.ans2);
        ans3=rootView.findViewById(R.id.ans3);
        ans4=rootView.findViewById(R.id.ans4);
        refInit = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                .child(Constants.uid)
                .child("questions")
                .child(Integer.toString(3));
        refInit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(ModelQuestion.class) != null){
                    ModelQuestion modelQuestion=dataSnapshot.getValue(ModelQuestion.class);
                    String questionstr=modelQuestion.getQuestion();
                    String ans1str=modelQuestion.getOptA();
                    String ans2str=modelQuestion.getOptB();
                    String ans3str=modelQuestion.getOptC();
                    String ans4str=modelQuestion.getOptD();
                    
                    question.setText(questionstr);
                    ans1.setText(ans1str);
                    ans2.setText(ans2str);
                    ans3.setText(ans3str);
                    ans4.setText(ans4str);
                    String correctAnswer = modelQuestion.getCorrectOption();
    
                    if(correctAnswer!=null && !correctAnswer.equals(""))
                    {
                        skipSelection = true;
                        if(correctAnswer.equals(ans1str))
                            color(ans1);
                        else
                        if(correctAnswer.equals(ans2str))
                            color(ans2);
                        else if(correctAnswer.equals(ans3str))
                            color(ans3);
                        else if(correctAnswer.equals(ans4str))
                            color(ans4);
        
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unColor(ans2);
                unColor(ans3);
                unColor(ans4);
                color(ans1);
                ansSelected=true;
                skipSelection = false;
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
                skipSelection = false;
            
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
                skipSelection = false;
            
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
                skipSelection = false;
            
            }
        });
    
        ImageView edit = rootView.findViewById(R.id.edit_question);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
                Intent intent = new Intent(getActivity(), EditQuestionTemplate.class);
                intent.putExtra(Constants.questionNumber, String.valueOf(3));
                startActivity(intent);
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
        String text = button.getText().toString();
        correctOption = new HashMap<>();
        
        correctOption.put("correctOption", text );
        
        button.setBackground(getResources().getDrawable(R.drawable.full_black_box));
        button.setTextColor(getResources().getColor(R.color.white));
    }
    
    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        if(skipSelection)
            callback.goToNextStep();
        else
        if (ansSelected) {
            // show progress and update the question on the DB
            
            dialog.show();
            refInit.updateChildren(correctOption).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    callback.goToNextStep();
                }
            });
            
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
