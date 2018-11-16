package com.testlabic.datenearu.QuestionUtils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
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

public class QuestionsActivity extends AppCompatActivity implements CardStackListener {
    
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private GoogleProgressBar progressBar;
    private CardStackView cardStackView;
    View skip;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        progressBar = findViewById(R.id.google_progress);
        progressBar.setVisibility(View.VISIBLE);
        skip = findViewById(R.id.like_button);
        setupCardStackView();
        setupButton();
        RelativeLayout rl = findViewById(R.id.rl);
        rl.setEnabled(false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    @Override
    public void onCardSwiped(Direction direction) {
        Log.d("CardStackView", "onCardSwiped: p = " + manager.getTopPosition() + ", d = " + direction);
       /* if (manager.getTopPosition() == adapter.getItemCount() - 5) {
            adapter.addSpots(createSpots());
            adapter.notifyDataSetChanged();
        }*/
    
        TextView quesNumber = findViewById(R.id.quesNumber);
        if(manager.getTopPosition()>9)
        {
            skip.setEnabled(false);
            quesNumber.setVisibility(View.GONE);
            /*
            Disable card switching
             */
            manager.setSwipeThreshold(1.0f);
        }
        else
        {
            skip.setEnabled(true);
            quesNumber.setVisibility(View.VISIBLE);
            manager.setSwipeThreshold(0.3f);
        }
        quesNumber.setText(String.valueOf((manager.getTopPosition()+1) + "/10"));
        
    }
    
    @Override
    public void onCardDragging(Direction direction, float ratio) {
    
    }
    
    @Override
    public void onCardRewound() {
        Log.d("CardStackView", "onCardRewound: " + manager.getTopPosition());
        TextView quesNumber = findViewById(R.id.quesNumber);
        quesNumber.setText(String.valueOf((manager.getTopPosition()+1) + "/10"));
        manager.setSwipeThreshold(0.3f);
        skip.setEnabled(true);
    }
    
    @Override
    public void onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled:" + manager.getTopPosition());
    }
    
    private void setupCardStackView() {
        refresh();
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
        
        View rewind = findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .build();
                manager.setRewindAnimationSetting(setting);
                cardStackView.rewind();
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
    
    private void downloadQuestions() {
        final List<ModelQuestion> questions = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.userInfo).child(Constants.uid)
                                .child(Constants.questions);
        
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        
                if(dataSnapshot.getValue()!=null)
                {
                    ModelQuestion item = dataSnapshot.getValue(ModelQuestion.class);
                    if(item!=null)
                        questions.add(item);
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
                adapter = new CardStackAdapter(getApplicationContext(), questions);
                cardStackView = findViewById(R.id.card_stack_view);
                cardStackView.setEnabled(false);
                cardStackView.setLayoutManager(manager);
                cardStackView.setAdapter(adapter);
                
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
    }
    
}