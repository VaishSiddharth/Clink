package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.NewQuestionUtils.QuestionsStepperAdapter;
import com.testlabic.datenearu.QuestionUtils.CardStackAdapter;
import com.testlabic.datenearu.QuestionUtils.ModelQuestion;
import com.testlabic.datenearu.QuestionUtils.QuestionsActivity;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.List;

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
        questionsStepperAdapter.createStep(5);
        questionsStepperAdapter.createStep(6);
        questionsStepperAdapter.createStep(7);
        questionsStepperAdapter.createStep(8);
        questionsStepperAdapter.createStep(9);
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
        new SweetAlertDialog(this)
                .setTitleText("Last step!")
                .setContentText("Answer these ten questions about yourself, these questions will then be used to get a match for you!")
                .setConfirmText("Yes, go ahead!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }
}
