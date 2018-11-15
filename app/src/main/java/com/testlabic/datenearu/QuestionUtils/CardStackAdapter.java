package com.testlabic.datenearu.QuestionUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.R;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Spot> spots;
    private Boolean clickedOptA = false,
            clickedOptB = false, clickedOptC = false, clickedOptD  = false;
    private Context context;
    
    public CardStackAdapter(Context context, List<Spot> spots) {
        this.inflater = LayoutInflater.from(context);
        this.spots = spots;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.question_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        /*Spot spot = spots.get(position);
        holder.name.setText(spot.name);
        holder.city.setText(spot.city);
        Glide.with(holder.image)
                .load(spot.url)
                .into(holder.image);*/
        
        holder.optA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                color if uncolored or vice versa!
                 */
                if(!clickedOptA)
                {
                    colorOpt(holder.optA);
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
                    colorOpt(holder.optB);
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
                    colorOpt(holder.optC);
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
                    colorOpt(holder.optD);
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
    
    private void colorOpt(TextView opt) {
        opt.setTextColor(context.getResources().getColor(R.color.white));
        opt.setBackground(context.getResources().getDrawable(R.drawable.solid_color_background));
    
        Log.e("CardStackAdapter", "Colored! opt "+opt.getText().toString());
    }
    
    @Override
    public int getItemCount() {
        return spots.size();
    }

    public void addSpots(List<Spot> spots) {
        this.spots.addAll(spots);
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