package com.testlabic.datenearu.QuestionUtils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testlabic.datenearu.R;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    
    private static final String TAG = CardStackAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private List<ModelQuestion> questions;
    private Boolean clickedOptA = false,
            clickedOptB = false, clickedOptC = false, clickedOptD  = false;
    private Context context;
    private static int score;
    private int currentPos = -1;
    private boolean subOnce = false;
    boolean selectedSomethingElse = false;
    
    public CardStackAdapter(Context context, List<ModelQuestion> questions) {
        this.inflater = LayoutInflater.from(context);
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.question_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,  int position) {
        ModelQuestion question = questions.get(position);
        holder.question.setText(question.getQuestion());
        holder.optA.setText(question.getOptA());
        holder.optB.setText(question.getOptB());
        holder.optC.setText(question.getOptC());
        holder.optD.setText(question.getOptD());
        
        /*
        Uncolor all options explicitly
         */
        
        unColorOpt(holder.optB);
        unColorOpt(holder.optA);
        unColorOpt(holder.optC);
        unColorOpt(holder.optD);
        
        holder.optA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                if(!clickedOptA)
                {
                    colorOpt(holder.optA, holder.getAdapterPosition());
                    unColorOpt(holder.optB);
                    unColorOpt(holder.optC);
                    unColorOpt(holder.optD);
                    clickedOptA = true;
                    
                    clickedOptB = false;
                    clickedOptC = false;
                    clickedOptD = false;
                }
                else
                    if(clickedOptA)
                    {
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
                if(!clickedOptB)
                {
                    colorOpt(holder.optB, holder.getAdapterPosition());
                    unColorOpt(holder.optA);
                    unColorOpt(holder.optC);
                    unColorOpt(holder.optD);
                    clickedOptB = true;
    
                    clickedOptA = false;
                    clickedOptC = false;
                    clickedOptD = false;
                }
                else
                if(clickedOptB)
                {
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
                if(!clickedOptC)
                {
                    colorOpt(holder.optC, holder.getAdapterPosition());
                    unColorOpt(holder.optB);
                    unColorOpt(holder.optA);
                    unColorOpt(holder.optD);
                    clickedOptC = true;
    
                    clickedOptB = false;
                    clickedOptA = false;
                    clickedOptD = false;
                }
                else
                if(clickedOptC)
                {
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
                if(!clickedOptD)
                {
                    colorOpt(holder.optD, holder.getAdapterPosition());
                    unColorOpt(holder.optB);
                    unColorOpt(holder.optC);
                    unColorOpt(holder.optA);
                    clickedOptD = true;
    
                    clickedOptB = false;
                    clickedOptC = false;
                    clickedOptA = false;
                }
                else
                if(clickedOptA)
                {
                    unColorOpt(holder.optA);
                    clickedOptD = false;
                }
            
            }
        });
        
    }
    
    private void unColorOpt(TextView opt) {
    
        opt.setTextColor(context.getResources().getColor(R.color.shade_black));
        opt.setBackground(context.getResources().getDrawable(R.drawable.border_color_box));
        
       
    }
    
    private void colorOpt(TextView opt, int position) {
    
       
        
        if(currentPos!=position)
            subOnce = false;
        
        opt.setTextColor(context.getResources().getColor(R.color.white));
        opt.setBackground(context.getResources().getDrawable(R.drawable.solid_color_background));
    
        Log.e("CardStackAdapter", "Colored! opt "+opt.getText().toString());
        
        String optedOption = opt.getText().toString();
        
        /*
        Match option and calculate marks;
         */
        
        if(optedOption.equals(questions.get(position).correctOption)) {
    
          
            if(currentPos!=position|| selectedSomethingElse)
            score++;
            currentPos = position;
            subOnce = false;
        }else
        {
           
            if(currentPos==position&!subOnce) {
                score--;
                subOnce = true;
                selectedSomethingElse = true;
            }
        }
        
        Log.e(TAG, "Score is " + score*10+"%");
        
        if(score>=8) {
            Intent i = new Intent(context, MatchSuccess.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
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