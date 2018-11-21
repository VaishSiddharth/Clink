package com.testlabic.datenearu.MessageUtils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.Models.ModelMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import model.AllModel;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    private ArrayList<ModelMessage> allModelArrayList;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    
    public MessagesAdapter(Context context, ArrayList<ModelMessage> allModelArrayList) {
        this.context = context;
        this.allModelArrayList = allModelArrayList;
    }
    
    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all,parent,false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
    
        final ModelMessage message = allModelArrayList.get(holder.getAdapterPosition());
        Glide.with(context).load(message.getImageUrl()).into(holder.image);
        holder.name.setText(message.getSendersName());
        holder.time.setText(SIMPLE_DATE_FORMAT.format(message.getTime()));
        holder.txt.setText(message.getText());
        
        holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        if (position==0){
            holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        }else if (position==1){
            holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        }else {
            holder.linear.setBackgroundResource(R.drawable.ract);
        }
        
        
        
        if (position==0){
            holder.n1.setVisibility(View.VISIBLE);
        }else if (position==1) {
            holder.n1.setVisibility(View.VISIBLE);
        }else {
            holder.n1.setVisibility(View.GONE);
        }
        
        if (position==2){
            holder.n2.setVisibility(View.VISIBLE);
        }else if (position==3){
            holder.n3.setVisibility(View.VISIBLE);
        }else if (position==4){
            holder.n4.setVisibility(View.VISIBLE);
        }
        
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
               /* Intent i = new Intent(context, ChatFullScreen.class);
                i.putExtra(Constants.chatName, message.getSendersName());
                context.startActivity(i);*/
            }
        });
        
    }
    
    @Override
    public int getItemCount() {
        return allModelArrayList.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,n2,n3,n4;
        TextView name,time,txt,n1;
        LinearLayout linear;
        
        
        public ViewHolder(View itemView) {
            super(itemView);
            
            image=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            txt=itemView.findViewById(R.id.txt);
            linear =itemView.findViewById(R.id.linear);
            
            n1=itemView.findViewById(R.id.n1);
            n2=itemView.findViewById(R.id.n2);
            n3=itemView.findViewById(R.id.n3);
            n4=itemView.findViewById(R.id.n4);
            
            
            
            
            
        }
    }
}
