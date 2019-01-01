package com.testlabic.datenearu.NewUserSetupUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.QuestionUtils.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardStackAdapterNewUser extends RecyclerView.Adapter<CardStackAdapterNewUser.ViewHolder> {
    
    private static final String TAG = CardStackAdapterNewUser.class.getSimpleName();
    private LayoutInflater inflater;
    private List<ModelQuestion> questions;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean clickedOptA = false,
            clickedOptB = false, clickedOptC = false, clickedOptD = false;
    private Context context;
    private ArrayList<DatabaseReference> referenceArrayList;
    private String clickedUid;
    
    public CardStackAdapterNewUser(Context context, List<ModelQuestion> questions, String clickedUid, ArrayList<DatabaseReference> referenceList) {
        this.inflater = LayoutInflater.from(context);
        this.questions = questions;
        this.context = context;
        this.clickedUid = clickedUid;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.referenceArrayList = referenceList;
        editor = sharedPreferences.edit();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.question_card, parent, false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        
        /*
        Show confirmation screen for position == 10
         */
        
        unColorOpt(holder.optB);
        unColorOpt(holder.optA);
        unColorOpt(holder.optC);
        unColorOpt(holder.optD);
        clickedOptA = false;
        clickedOptB = false;
        clickedOptC = false;
        clickedOptD = false;
        
        if (position == 10) {
            holder.question.setText(context.getResources().getString(R.string.confirmation2));
            
            holder.optA.setText("Yes!");
            
            holder.optA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //duplicate the user info here.
                    DuplicateUserInfoToCityLabelNode();
                    UpdateXPoints();
                }
            });
            
            holder.optB.setVisibility(View.GONE);
            holder.optC.setText(context.getResources().getString(R.string.rewind_msg));
            holder.optC.setBackgroundDrawable(null);
            holder.optC.setTextSize(14.0f);
            holder.optC.setEnabled(false);
            holder.optD.setVisibility(View.GONE);
            
            //show a sweet alert dialog here!
           
        } else {
            
            final ModelQuestion question = questions.get(position);
            holder.question.setText(question.getQuestion());
            holder.optA.setText(question.getOptA());
            holder.optB.setText(question.getOptB());
            holder.optC.setText(question.getOptC());
            holder.optD.setText(question.getOptD());
            
            holder.optA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    unColorOpt(holder.optB);
                    colorOpt(holder.optA, holder.getAdapterPosition());
                    unColorOpt(holder.optC);
                    unColorOpt(holder.optD);
                    
                }
            });
            
            holder.optB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    unColorOpt(holder.optA);
                    colorOpt(holder.optB, holder.getAdapterPosition());
                    unColorOpt(holder.optC);
                    unColorOpt(holder.optD);
                    
                }
            });
            
            holder.optC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    unColorOpt(holder.optB);
                    colorOpt(holder.optC, holder.getAdapterPosition());
                    unColorOpt(holder.optA);
                    unColorOpt(holder.optD);
                    
                }
            });
            
            holder.optD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    unColorOpt(holder.optB);
                    colorOpt(holder.optD, holder.getAdapterPosition());
                    unColorOpt(holder.optC);
                    unColorOpt(holder.optA);
                }
            });
            
        }
    }
    
    private void UpdateXPoints() {
        Constants.uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        HashMap<String, Object> updatePoints = new HashMap<>();
        updatePoints.put(Constants.xPoints, 1000);
        reference.updateChildren(updatePoints);
    }
    
    private void unColorOpt(TextView opt) {
        
        opt.setTextColor(context.getResources().getColor(R.color.shade_black));
        opt.setBackground(context.getResources().getDrawable(R.drawable.border_color_box));
        
    }
    
    private void colorOpt(TextView opt, int position) {
        
        opt.setTextColor(context.getResources().getColor(R.color.white));
        opt.setBackground(context.getResources().getDrawable(R.drawable.solid_color_background));
        
        String correctAnswer = opt.getText().toString();
        HashMap<String, Object> updateCorrectAns = new HashMap<>();
        updateCorrectAns.put("correctOption", correctAnswer);
        DatabaseReference reference = referenceArrayList.get(position);
        reference.updateChildren(updateCorrectAns);
        Log.e("CardStackAdapter", "Colored! opt " + opt.getText().toString() + " position: " + position);
        
    }
    
    @Override
    public int getItemCount() {
        return questions.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView optA;
        TextView optB;
        TextView optC;
        TextView optD;
        TextView question;
        View completeItem;
        
        ViewHolder(View view) {
            super(view);
            this.optA = view.findViewById(R.id.optA);
            this.optB = view.findViewById(R.id.optB);
            this.optC = view.findViewById(R.id.optC);
            this.optD = view.findViewById(R.id.optD);
            this.question = view.findViewById(R.id.questionText);
            this.completeItem = view;
        }
    }
    
    private void DuplicateUserInfoToCityLabelNode() {
        
        {
            DatabaseReference refInit = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(Constants.uid);
            refInit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(ModelUser.class) != null) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            String gender = user.getGender();
                             String cityLabel = user.getCityLabel();
                            if (cityLabel != null && gender != null) {
                                cityLabel = cityLabel.replace(", ", "_");
                                DatabaseReference refFin = FirebaseDatabase.getInstance().getReference().child(Constants.cityLabels)
                                        .child(cityLabel).child(gender).child(Constants.uid);
                                final String finalCityLabel = cityLabel;
                                refFin.setValue(dataSnapshot.getValue(ModelUser.class)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString(Constants.cityLabel, finalCityLabel).apply();
                
                                        //show success message and then
                                        //move to main activity now!
                
                                       /* sweetAlertDialog
                                                .setTitleText("Done!")
                                                .setContentText("Best of luck!")
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
                                        Intent i = new Intent(context, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(i);
                                    }
                                });
                            }
                        }
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    
}