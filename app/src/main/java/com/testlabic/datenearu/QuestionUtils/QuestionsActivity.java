package com.testlabic.datenearu.QuestionUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelSubscr;
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
import java.util.HashMap;
import java.util.List;

import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuestionsActivity extends AppCompatActivity implements CardStackListener {
    
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private GoogleProgressBar progressBar;
    private CardStackView cardStackView;
    private boolean rewindPointEnable = false;
    private String clickedUid;
    private TextView xPoints;
    private TextView rewindPenalty;
    View skip;
    
    @Override
    public void onBackPressed() {
        finish();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        progressBar = findViewById(R.id.google_progress);
        rewindPenalty= findViewById(R.id.rewindPenalty);
        xPoints = findViewById(R.id.xpoints);
        fillXPoints();
        rewindPenalty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        skip = findViewById(R.id.like_button);
        clickedUid = getIntent().getStringExtra(Constants.clickedUid);
        if(clickedUid==null||clickedUid.equals(""))
            clickedUid = Constants.uid;
        setupCardStackView();
        setupButton();
        RelativeLayout rl = findViewById(R.id.rl);
        rl.setEnabled(false);
    }
    
    private void fillXPoints() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                    if(modelSubscr!=null)
                    {
                        int current = modelSubscr.getXPoints();
                         xPoints.setText(String.valueOf(current)+" x's");
                    }
                }
            }
        
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
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
       
       if(manager.getTopPosition()>0) {
           rewindPointEnable= true;
           rewindPenalty.setVisibility(View.VISIBLE);
       }
       
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
    public void onCardDragging(Direction direction, float ratio) {
    
    }
    
    @Override
    public void onCardRewound() {
        Log.d("CardStackView", "onCardRewound: " + manager.getTopPosition());
        TextView quesNumber = findViewById(R.id.quesNumber);
        quesNumber.setText(String.valueOf((manager.getTopPosition() + 1) + "/10"));
        manager.setSwipeThreshold(0.3f);
        skip.setEnabled(true);
        quesNumber.setVisibility(View.VISIBLE);
        /*if(adapter!=null)
            adapter.notifyItemChanged(manager.getTopPosition());*/
        if(manager.getTopPosition()>0) {
            rewindPointEnable= true;
            rewindPenalty.setVisibility(View.VISIBLE);
        }
        else if(manager.getTopPosition()==0){
            rewindPointEnable= false;
            rewindPenalty.setVisibility(View.GONE);
    
        }
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
        
        final View rewind = findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Show a dialog to deduce 50 x points and then if confirmed continue
                 */
                if(rewindPointEnable)
                showDialog();
                
                else
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
    
    private void showDialog() {
    
        new SweetAlertDialog(this)
                .setTitleText("Rewind?")
                .setContentText("Rewind will cost you 500 points continue?")
                .setConfirmText("Yes!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        /*
                        Update the points in database and rewind!
                         */
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.xPoints)
                                .child(Constants.uid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                                    if(modelSubscr!=null)
                                    {
                                        int current = modelSubscr.getXPoints();
                                        current -= 50;
                                        HashMap<String, Object> updatePoints = new HashMap<>();
                                        updatePoints.put(Constants.xPoints, current);
                                        dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                rewindCard();
                                            }
                                        });
                    
                                    }
                                }
                            }
        
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
            
                            }
                        });
                    }
                }).showCancelButton(true).show();
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
    
    private void downloadQuestions() {
        if (clickedUid != null) {
        
        final List<ModelQuestion> questions = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(clickedUid)
                .child(Constants.questions);
        
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                if (dataSnapshot.getValue() != null) {
                    ModelQuestion item = dataSnapshot.getValue(ModelQuestion.class);
                    if (item != null)
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
                adapter = new CardStackAdapter(getApplicationContext(), questions, clickedUid);
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