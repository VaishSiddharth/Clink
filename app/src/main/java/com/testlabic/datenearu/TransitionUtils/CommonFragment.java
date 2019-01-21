package com.testlabic.datenearu.TransitionUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.QuestionUtils.QuestionsActivity;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import org.w3c.dom.Text;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener {
    private static final String TAG = CommonFragment.class.getSimpleName();
    private ImageView imageView;
    private TextView name, age, oneLine;
    private RatingBar ratingBar;
    FloatingActionButton message, report, match;
    private String imageUrl, nameS, ageS , sendersUid, oneLineS, gender;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.image);
        Glide.with(CommonFragment.this).load(imageUrl).into(imageView);
        Log.e(TAG, "imageUrl is"+ imageUrl);
        name = rootView.findViewById(R.id.name);
        age = rootView.findViewById(R.id.age);
        oneLine = rootView.findViewById(R.id.oneLine);
        message = rootView.findViewById(R.id.message_fab);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Under Progress", Toast.LENGTH_SHORT).show();
            }
        });
        match = rootView.findViewById(R.id.attempt_match);
        match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showDialog();
            }
        });
        name.setText(nameS);
        age.setText("'"+ageS);
        //TODO: Add imageview for gender, next to age and show image likewise (as in
        

        if(oneLineS!=null)
            oneLine.setText(oneLineS);

        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
     
        Intent i = new Intent(getActivity(), ClickedUser.class);
        i.putExtra(Constants.clickedUid, sendersUid);
        i.putExtra(Constants.imageUrl, imageUrl);
        startActivity(i);
    }

    public void bindData(ModelUser user) {
        this.imageUrl = user.getImageUrl();
        this.nameS = user.getUserName();
        this.ageS = ""+user.getAge();
        this.sendersUid = user.getUid();
        this.oneLineS = user.getOneLine();
        this.gender = user.getGender();
    }
    private void showDialog() {
        
        new SweetAlertDialog(getActivity())
                .setTitleText("Attempt match?")
                .setContentText("You will have to answer ten questions, and if you win you get a chance to connect, it will cost you 100 points continue")
                .setConfirmText("Yes!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        sDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                        final DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.xPoints)
                                .child(Constants.uid);
                        
                        xPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                
                                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                                if (modelSubscr != null) {
                                    int current = modelSubscr.getXPoints();
                                    if (current < Constants.attemptTestPoints) {
                                        Toast.makeText(getActivity(), "You don't have enough points, buy now!", Toast.LENGTH_SHORT).show();
                                        BuyPoints();
                                    } else {
                                        current -= Constants.attemptTestPoints;
                                        HashMap<String, Object> updatePoints = new HashMap<>();
                                        updatePoints.put(Constants.xPoints, current);
                                        dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sDialog
                                                        .setTitleText("Starting!")
                                                        .setContentText("Best of luck!")
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(new Intent(getActivity(), QuestionsActivity.class).putExtra(Constants.clickedUid, sendersUid));
                                                        sDialog.dismiss();
                                                    }
                                                }, 2500);
                                            }
                                        });
                                        
                                    }
                                }
                                
                            }
                            
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            
                            }
                        });
                        
                        ///
                    }
                })
                .show();
        
    }
    
    private void BuyPoints() {
        startActivity(new Intent(getActivity(), PurchasePacks.class));
    }
    
}
