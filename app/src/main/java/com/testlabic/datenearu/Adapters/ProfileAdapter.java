package com.testlabic.datenearu.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testlabic.datenearu.Activities.EditActivity;
import com.testlabic.datenearu.Activities.Settings;
import com.testlabic.datenearu.Activities.Transparent_gift_Activity;
import com.testlabic.datenearu.NewQuestionUtils.QuestionSetup;
import com.testlabic.datenearu.ProfileUtils.AboutEditor;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;

import model.ProfileModel;

/**
 * Created by wolfsoft4 on 20/9/18.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    Context context;
    private ArrayList<ProfileModel> profileModelArrayList;

    public ProfileAdapter(Context context, ArrayList<ProfileModel> profileModelArrayList) {
        this.context = context;
        this.profileModelArrayList = profileModelArrayList;
    }


    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {

        holder.inbox.setImageResource(profileModelArrayList.get(position).getInbox());
        holder.arrow.setImageResource(profileModelArrayList.get(position).getArrow());
        holder.txttrades.setText(profileModelArrayList.get(position).getTxttrades());
        holder.txthistory.setText(profileModelArrayList.get(position).getTxthistory());
        
        /*
        setting up clicks for items in profile section
         */
        
        if(position==0)
        {
            /*
            Code questions part here
             */
            SharedPreferences prefs = context.getSharedPreferences(Constants.userDetailsOff, Context.MODE_PRIVATE);
    
            boolean isQuestionaireComplete = prefs.getBoolean(Constants.isQuestionaireComplete+Constants.uid, false);
    
            if(!isQuestionaireComplete)
            {
                //TODO: Display badge on holder.profileItem
                holder.arrow.setVisibility(View.INVISIBLE);
                holder.badge.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.arrow.setVisibility(View.VISIBLE);
                holder.badge.setVisibility(View.INVISIBLE);
            }
            holder.profileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, QuestionSetup.class).putExtra(Constants.editQuestions, true));
                }
            });
           
        }
        
        if(position ==1)
        {
            holder.profileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, EditActivity.class));
                }
            });
        }
        
        if(position==2)
        {
            /*
            Open about editor and make changes..
             */
            SharedPreferences prefs = context.getSharedPreferences(Constants.userDetailsOff, Context.MODE_PRIVATE);
            
            boolean isAboutComplete = prefs.getBoolean(Constants.isAboutComplete+Constants.uid, false);
    
            if(!isAboutComplete)
            {
                //TODO: Display badge on holder.profileItem
                holder.arrow.setVisibility(View.INVISIBLE);
                holder.badge.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.arrow.setVisibility(View.VISIBLE);
                holder.badge.setVisibility(View.INVISIBLE);
            }
            holder.profileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, AboutEditor.class));
                }
            });
            
        }
        
        if(position==3)
        {
            holder.profileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, Settings.class));
                }
            });
          
        }

    }

    @Override
    public int getItemCount() {
        return profileModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView inbox,arrow;
        LinearLayout profileItem;
        TextView txttrades,txthistory, badge;

        public ViewHolder(View itemView) {
            super(itemView);

            inbox=itemView.findViewById(R.id.inbox);
            arrow=itemView.findViewById(R.id.arrow);
            txttrades=itemView.findViewById(R.id.txttrades);
            txthistory=itemView.findViewById(R.id.txthistory);
            profileItem = itemView.findViewById(R.id.profileItem);
            badge = itemView.findViewById(R.id.badge);
            
            
        }
    }
}
