package com.testlabic.datenearu.QuestionUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testlabic.datenearu.AttemptMatchUtils.MatchCalculator;
import com.testlabic.datenearu.Models.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    
    private static final String TAG = CardStackAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private List<ModelQuestion> questions;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean clickedOptA = false,
            clickedOptB = false, clickedOptC = false, clickedOptD = false;
    private Context context;
    private int score = 0;
    private int currentPos = -1;
    private boolean subOnce = false;
    private boolean selectedSomethingElse = false;
    private String clickedUid;
    
    public CardStackAdapter(Context context, List<ModelQuestion> questions, String clickedUid) {
        this.inflater = LayoutInflater.from(context);
        this.questions = questions;
        this.context = context;
        this.clickedUid = clickedUid;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        
        Log.e(TAG, "Method called for position "+holder.getAdapterPosition());
        
        unColorOpt(holder.optB);
        unColorOpt(holder.optA);
        unColorOpt(holder.optC);
        unColorOpt(holder.optD);
        clickedOptA = false;
        clickedOptB = false;
        clickedOptC = false;
        clickedOptD = false;
        
        position = holder.getAdapterPosition();
        
        if(position==9)
        {
            clickedOptA = false;
            clickedOptB = false;
            clickedOptC = false;
            clickedOptD = false;
        }
        
        if (position == 10) {
            holder.question.setText(context.getResources().getString(R.string.confirmation));
            
            holder.optA.setText("Yes!");
            
            holder.optA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    Intent i = new Intent(context, MatchCalculator.class);
                    i.putExtra(Constants.score, score);
                    i.putExtra(Constants.clickedUid, clickedUid);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
            
            holder.optB.setVisibility(View.GONE);
            
            holder.optC.setText(context.getResources().getString(R.string.rewind_msg));
            holder.optC.setBackgroundDrawable(null);
            holder.optC.setTextSize(14.0f);
            
            holder.optC.setEnabled(false);
            holder.optD.setVisibility(View.GONE);
        } else {
            
            final ModelQuestion question = questions.get(position);
            holder.question.setText(question.getQuestion());
            holder.optA.setText(question.getOptA());
            holder.optB.setText(question.getOptB());
            holder.optC.setText(question.getOptC());
            holder.optD.setText(question.getOptD());
        /*
        Uncolor sample_last_message options explicitly
         */
        
        /*
        Color the option if previously chosen
         */
            String savedColoredOpt = question.getSelectedOption();
            /*if(savedColoredOpt!=null) {
    
                Log.e(TAG, "The saved colored option is " + savedColoredOpt);
                if (savedColoredOpt.equals(question.getOptA()))
                    colorOpt(holder.optA, holder.getAdapterPosition(), question);
                else if (savedColoredOpt.equals(question.getOptB()))
                    colorOpt(holder.optB, holder.getAdapterPosition(), question);
                if (savedColoredOpt.equals(question.getOptC()))
                    colorOpt(holder.optC, holder.getAdapterPosition(), question);
                if (savedColoredOpt.equals(question.getOptD()))
                    colorOpt(holder.optD, holder.getAdapterPosition(), question);
    
            }*/
            
            
            holder.optA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    if (!clickedOptA) {
                        colorOpt(holder.optA, holder.getAdapterPosition(), question);
                        unColorOpt(holder.optB);
                        unColorOpt(holder.optC);
                        unColorOpt(holder.optD);
                        clickedOptA = true;
                        
                        clickedOptB = false;
                        clickedOptC = false;
                        clickedOptD = false;
                    } else if (clickedOptA) {
                        unColorOpt(holder.optA);
                        clickedOptA = false;
                    }
                    
                }
            });
            
            holder.optB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    if (!clickedOptB) {
                        colorOpt(holder.optB, holder.getAdapterPosition(), question);
                        unColorOpt(holder.optA);
                        unColorOpt(holder.optC);
                        unColorOpt(holder.optD);
                        clickedOptB = true;
                        
                        clickedOptA = false;
                        clickedOptC = false;
                        clickedOptD = false;
                    } else if (clickedOptB) {
                        unColorOpt(holder.optB);
                        clickedOptB = false;
                    }
                    
                }
            });
            
            holder.optC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    if (!clickedOptC) {
                        colorOpt(holder.optC, holder.getAdapterPosition(), question);
                        unColorOpt(holder.optB);
                        unColorOpt(holder.optA);
                        unColorOpt(holder.optD);
                        clickedOptC = true;
                        
                        clickedOptB = false;
                        clickedOptA = false;
                        clickedOptD = false;
                    } else if (clickedOptC) {
                        unColorOpt(holder.optC);
                        clickedOptC = false;
                    }
                    
                }
            });
            
            holder.optD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                    if (!clickedOptD) {
                        colorOpt(holder.optD, holder.getAdapterPosition(), question);
                        unColorOpt(holder.optB);
                        unColorOpt(holder.optC);
                        unColorOpt(holder.optA);
                        clickedOptD = true;
                        
                        clickedOptB = false;
                        clickedOptC = false;
                        clickedOptA = false;
                    } else if (clickedOptD) {
                        unColorOpt(holder.optD);
                        clickedOptD = false;
                    }
                    
                }
            });
            
        }
    }
    
    
    private void unColorOpt(TextView opt) {
        
        opt.setTextColor(context.getResources().getColor(R.color.shade_black));
        opt.setBackground(context.getResources().getDrawable(R.drawable.border_color_box));
        
    }
    
    private void colorOpt(TextView opt, int position, ModelQuestion question) {
        
        /*
        Save the value in shared preference to show it when user goes rewind!
         */
        
        question.setSelectedOption(opt.getText().toString());
        if (currentPos != position)
            subOnce = false;
        
        opt.setTextColor(context.getResources().getColor(R.color.white));
        opt.setBackground(context.getResources().getDrawable(R.drawable.solid_color_background));
        
        Log.e("CardStackAdapter", "Colored! opt " + opt.getText().toString() + " position: "+position);
        
        String optedOption = opt.getText().toString();
        
        /*
        Match option and calculate marks;
         */
        
        if (optedOption.equals(question.correctOption)) {
            if (currentPos != position || selectedSomethingElse)
                score++;
            currentPos = position;
            subOnce = false;
        } else {
            if (currentPos == position & !subOnce) {
                score--;
                subOnce = true;
                selectedSomethingElse = true;
            }
        }
        Log.e(TAG, "Score is " + score * 10 + "%");
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
    
}