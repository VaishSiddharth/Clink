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
import com.testlabic.datenearu.ClickedUser;
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

public class QuestionsEnteringNewUser extends AppCompatActivity implements CardStackListener {
    
    private CardStackLayoutManager manager;
    private CardStackAdapterNewUser adapter;
    private CardStackView cardStackView;
    private GoogleProgressBar progressBar;
    private ArrayList<DatabaseReference> referenceList = new ArrayList<>();
    View skip;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_entering_new_user);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                showInstructionDialog();
    
            }
        }, 1000);
        skip = findViewById(R.id.like_button);
        progressBar = findViewById(R.id.google_progress);
        setupCardStackView();
        setupButton();
    }
    
    private void setupCardStackView() {
        refresh();
    }
    
    private void rewindCard() {
        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .build();
        manager.setRewindAnimationSetting(setting);
        cardStackView.rewind();
    }
    
    private void refresh() {
        manager = new CardStackLayoutManager(getApplicationContext(), this);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        downloadQuestions();
        // manager.setCanScrollHorizontal(true);
        // manager.setCanScrollVertical(true);
        
    }
    
    private void setupButton() {
        View skip = findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });
        
        final View rewind = findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    rewindCard();
            }
        });
        
        View like = findViewById(R.id.like_button);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });
    }
    
    private void showInstructionDialog() {
        new SweetAlertDialog(this)
                .setTitleText("Last step!")
                .setContentText("You have to answer these ten questions about yourself, these questions will then be used to get a match for you!")
                .setConfirmText("Yes, go ahead!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
               
                .show();
    }
    
    @Override
    public void onCardDragging(Direction direction, float ratio) {
    
    }
    
    @Override
    public void onCardSwiped(Direction direction) {
    
        Log.d("CardStackView", "onCardSwiped: p = " + manager.getTopPosition() + ", d = " + direction);
       
        TextView quesNumber = findViewById(R.id.quesNumber);
        if (manager.getTopPosition() > 9) {
            skip.setEnabled(false);
            quesNumber.setVisibility(View.GONE);
            /*
            Disable card switching
             */
            manager.setSwipeThreshold(1.0f);
        } else {
        
            skip.setEnabled(true);
            quesNumber.setVisibility(View.VISIBLE);
            manager.setSwipeThreshold(0.3f);
        }
        quesNumber.setText(String.valueOf((manager.getTopPosition() + 1) + "/10"));
    }
    
    @Override
    public void onCardRewound() {
        Log.d("CardStackView", "onCardRewound: " + manager.getTopPosition());
        TextView quesNumber = findViewById(R.id.quesNumber);
        quesNumber.setText(String.valueOf((manager.getTopPosition() + 1) + "/10"));
        manager.setSwipeThreshold(0.3f);
        skip.setEnabled(true);
        quesNumber.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onCardCanceled() {
    
    }
    
    private void downloadQuestions() {
        if (Constants.uid != null) {
            
            final List<ModelQuestion> questions = new ArrayList<>();
            referenceList = new ArrayList<>();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo).child(Constants.uid)
                    .child(Constants.questions);
            
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    
                    if (dataSnapshot.getValue() != null) {
                        ModelQuestion item = dataSnapshot.getValue(ModelQuestion.class);
                        if (item != null) {
                            questions.add(item);
                            referenceList.add(dataSnapshot.getRef());
                        }
                    }
                }
                
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                }
                
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                
                }
                
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                populate adapter...
                 */
                    progressBar.setVisibility(View.GONE);
                    adapter = new CardStackAdapterNewUser(getApplicationContext(), questions, Constants.uid, referenceList);
                    cardStackView = findViewById(R.id.card_stack_view);
                    cardStackView.setEnabled(false);
                    cardStackView.setLayoutManager(manager);
                    cardStackView.setAdapter(adapter);
                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                        }
                    });
                    
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        }
    }
    
    
    
}
