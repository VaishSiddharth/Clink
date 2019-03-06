package com.testlabic.datenearu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.ChatUtils.ChatMessage;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.ChatUtils.temporaryChatFullScreen;
import com.testlabic.datenearu.Models.ModelLastMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.Date;

public class LastMessageAdapter extends RecyclerView.Adapter<LastMessageAdapter.ViewHolder> {
    private static final String TAG = LastMessageAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<ModelLastMessage> allModelArrayList;
    private DatabaseReference onlineStatusRef;
    private ValueEventListener onlineStatusListener;
    
    private String setTime(long timestampCreatedLong) {
        Date dateObj = new Date(timestampCreatedLong);
        long epoch = dateObj.getTime();
        CharSequence teste = DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        return (String) teste;
    }
    
    public LastMessageAdapter(Context context, ArrayList<ModelLastMessage> allModelArrayList) {
        this.context = context;
        this.allModelArrayList = allModelArrayList;
        //sort the list here
        
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_last_message, parent, false);
        return new ViewHolder(view);
    }
    
    private void setUpOnlineStatus(String contactUid, final ImageView onlineStatus) {
        
        onlineStatusRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.usersStatus).child(contactUid)
                .child("status");
        
        onlineStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                if (dataSnapshot.exists() && dataSnapshot.getValue(String.class) != null) {
                    String stat = dataSnapshot.getValue(String.class);
                    // Log.e(TAG, "The user "+sendersUid+ " is "+stat);
                    if (stat != null) {
                        if (stat.equalsIgnoreCase(Constants.online)) {
                            // Log.e(TAG, "Showing srch ");
                            onlineStatus.setVisibility(View.VISIBLE);
                        } else if (stat.equalsIgnoreCase(Constants.offline))
                            onlineStatus.setVisibility(View.INVISIBLE);
                    } else
                        onlineStatus.setVisibility(View.GONE);
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        };
        onlineStatusRef.addValueEventListener(onlineStatusListener);
    }
    
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        
        final ModelLastMessage lastMessage = allModelArrayList.get(position);
        holder.name.setText(allModelArrayList.get(position).getName());
        Glide.with(context).load(lastMessage.getImageUrl()).into(holder.image);
        
        setUpOnlineStatus(allModelArrayList.get(position).getUid(), holder.onlineStat);
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS + Constants.unread)
                .child(Constants.uid + Constants.unread)
                .child(lastMessage.getUid());
       // Log.e(TAG , "assl referes "+reference);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreads = (int) dataSnapshot.getChildrenCount();
                if (unreads > 0) {
                    holder.numberOfNewMesssages.setVisibility(View.VISIBLE);
                    holder.numberOfNewMesssages.setText(String.valueOf(unreads));
                    //holder.linear.setBackgroundResource(R.drawable.rect_white_border);
                    Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/SF-Pro-Display-Bold.otf");
                    
                    Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/SF-Pro-Display-Regular.otf");
                    holder.name.setTypeface(tf);
                    holder.time.setTypeface(tf2);
                    holder.time.setTextColor(context.getResources().getColor(R.color.shade_black));
                    holder.name.setTextColor(context.getResources().getColor(R.color.shade_black));
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.CHATS + Constants.unread)
                            .child(Constants.uid + Constants.unread)
                            .child(lastMessage.getUid());
                    setUpLastMessage(holder.txt, ref, holder.readStat, holder.replysymbol, holder.time, position);
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.CHATS)
                            .child(Constants.uid)
                            .child(lastMessage.getUid());
                   // Log.e(TAG, "refs "+databaseReference);
                    holder.linear.setBackgroundResource(R.drawable.ract);
                    setUpLastMessage(holder.txt, databaseReference, holder.readStat,
                            holder.replysymbol, holder.time, position);
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastMessage.getTemporaryContact()!=null&&lastMessage.getTemporaryContact())
                {
                    Intent i = new Intent(context, temporaryChatFullScreen.class);
                    i.putExtra(Constants.sendToUid, lastMessage.getUid());
                    i.putExtra(Constants.sendToName, lastMessage.getName());
                    if(lastMessage.getTempUid()!=null)
                    i.putExtra(Constants.tempUid, lastMessage.getTempUid());
                    context.startActivity(i);
                }
                else
                {
                    Intent i = new Intent(context, chatFullScreen.class);
                    i.putExtra(Constants.sendToUid, lastMessage.getUid());
                    i.putExtra(Constants.sendToName, lastMessage.getName());
                    context.startActivity(i);
                }
               
            }
        });
    }
    
    private void setUpLastMessage(final TextView txt, DatabaseReference databaseReference
            , final ImageView readStat, final ImageView replysymbol, final TextView time, final int position) {
        
        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //Log.e(TAG, "This "+ dataSnapshot.getRef()+" doesn't exist");
                   // Log.e(TAG, "Found in  " + position);
                  //  allModelArrayList.remove(position);
                  //  notifyDataSetChanged();
                }
               // Log.e(TAG, "The snaps " + String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot one : dataSnapshot.getChildren()) {
                    if (one.getValue(ChatMessage.class) != null) {
                        final String lastMessage = one.getValue(ChatMessage.class).getMessage();
                        final long timeStamp = one.getValue(ChatMessage.class).getSendingTime();
                        final Boolean isDelivered = one.getValue(ChatMessage.class).getMessageDelivered();
                        final String sendersUid = one.getValue(ChatMessage.class).getSentFrom();
                        final Boolean successfullySent = one.getValue(ChatMessage.class).getSuccessfullySent();
                        //Log.e(TAG, "The last message is "+lastMessage+" "+sendersUid);
                       
                            txt.setText(lastMessage);
                            time.setText(setTime(timeStamp));
                            if (sendersUid.equals(Constants.uid)) {
                                //For own message show delivered status
                                
                                replysymbol.setVisibility(View.VISIBLE);
                                readStat.setVisibility(View.GONE);
                                if (successfullySent != null && successfullySent) {
                                    // single tick for sent message!
                                    readStat.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_grey));
                                } else
                                    readStat.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_tick));
                                
                                if (isDelivered)
                                    readStat.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check));
                                else
                                    readStat.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_grey));
                                
                            } else {
                                readStat.setVisibility(View.GONE);
                                replysymbol.setVisibility(View.GONE);
                                
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
    public int getItemCount() {
        return allModelArrayList.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, readStat, onlineStat, n4, replysymbol;
        TextView name, time, txt, numberOfNewMesssages;
        LinearLayout linear;
        
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            txt = itemView.findViewById(R.id.txt);
            linear = itemView.findViewById(R.id.linear);
            replysymbol = itemView.findViewById(R.id.replysymbol);
            onlineStat = itemView.findViewById(R.id.onlineStatus);
            readStat = itemView.findViewById(R.id.readStat);
            numberOfNewMesssages = itemView.findViewById(R.id.numberOfNewMessages);
            
        }
    }
}
