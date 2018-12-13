package com.testlabic.datenearu;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.QuestionUtils.QuestionsActivity;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import adapter.View_Pager_Adapter;
import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;


public class ClickedUser extends AppCompatActivity implements View.OnClickListener {
    
    private static final String TAG = ClickedUser.class.getSimpleName();
    ImageView f1;
    boolean first = true;
    String imageUrl;
    private TextView name, about, age;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private View_Pager_Adapter view_pager_adapter;
    private String clickedUid;
    private TextView attemptMatch;
    private onImageUrlReceivedListener listener;
    
    public interface onImageUrlReceivedListener{
        void onDataReceived(String imageUrl);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_user);
        
        
        f1 = findViewById(R.id.f1);
        f1.setOnClickListener(this);
        clickedUid = getIntent().getStringExtra(Constants.clickedUid);
        imageUrl = getIntent().getStringExtra(Constants.imageUrl);
        if(clickedUid!=null)
            setUpDetails();
        
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleindicator);
        attemptMatch = findViewById(R.id.attempt_match);
        name = findViewById(R.id.name);
        about = findViewById(R.id.about);
        view_pager_adapter = new View_Pager_Adapter(getSupportFragmentManager(),imageUrl);
        viewPager.setAdapter(view_pager_adapter);
        circleIndicator.setViewPager(viewPager);
        // view_pager_adapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        
        attemptMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showDialog();
            }
        });
    }
    
    private void showDialog() {
    
        new SweetAlertDialog(this)
                .setTitleText("Attempt match?")
                .setContentText("You will have to answer ten questions, and if you win you get a chance to connect, it will cost you 100 x points continue")
                .setConfirmText("Yes!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        startActivity(new Intent(ClickedUser.this, QuestionsActivity.class).putExtra(Constants.clickedUid, clickedUid));
                    }
                })
                .show();
        
    }
    
    private void setUpDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(clickedUid);
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null && user.getUserName() != null)
                        name.setText(user.getUserName());
                    
                    if (user != null && user.getAbout() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            about.setText(Html.fromHtml(user.getAbout(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            about.setText(Html.fromHtml(user.getAbout()));
                        }
                    }
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    @Override
    public void onClick(View view) {
        if (first) {
            f1.setBackgroundResource(R.drawable.ic_like_1);
            first = false;
        } else {
            f1.setBackgroundResource(R.drawable.ic_like_heart_outline);
            first = true;
        }
    }
}