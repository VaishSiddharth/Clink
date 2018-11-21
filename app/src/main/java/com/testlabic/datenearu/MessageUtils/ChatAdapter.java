package com.testlabic.datenearu.MessageUtils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = ChatAdapter.class.getSimpleName();
    Context context;
    private ArrayList<ModelMessage> allModelArrayList;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    
    public ChatAdapter(Context context, ArrayList<ModelMessage> allModelArrayList) {
        this.context = context;
        this.allModelArrayList = allModelArrayList;
    }
    
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_msg_received,parent,false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
    
        final ModelMessage message = allModelArrayList.get(holder.getAdapterPosition());
        Glide.with(context).load(message.getImageUrl()).into(holder.image);
        holder.time.setText(SIMPLE_DATE_FORMAT.format(message.getTime()));
        holder.txt.setText(message.getText());
        Log.e(TAG, message.getText());
    }
    
    @Override
    public int getItemCount() {
        return allModelArrayList.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name,time,txt;
        
        
        public ViewHolder(View itemView) {
            super(itemView);
            
            image=itemView.findViewById(R.id.receivingImage);
            
        }
    }
}
