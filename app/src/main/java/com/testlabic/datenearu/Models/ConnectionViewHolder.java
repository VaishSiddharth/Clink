package com.testlabic.datenearu.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.ChatUtils.temporaryChatFullScreen;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Utils;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConnectionViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ConnectionViewHolder.class.getSimpleName();
    View v;
    private DatabaseReference onlineStatusRef;
    private ValueEventListener onlineStatusListener;
    
    public ConnectionViewHolder(@NonNull View itemView) {
        super(itemView);
        v = itemView;
    }
    
    private void setUpOnlineStatus(String contactUid, final ImageView onlineStatus) {
        
        onlineStatusRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.usersStatus).child(contactUid)
                .child("status");
        
        onlineStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                if(dataSnapshot.exists()&&dataSnapshot.getValue(String.class)!=null)
                {
                    String stat = dataSnapshot.getValue(String.class);
                    // Log.e(TAG, "The user "+sendersUid+ " is "+stat);
                    if (stat != null) {
                        if(stat.equalsIgnoreCase(Constants.online)) {
                            // Log.e(TAG, "Showing srch ");
                            onlineStatus.setVisibility(View.VISIBLE);
                        } else if(stat.equalsIgnoreCase(Constants.offline))
                            onlineStatus.setVisibility(View.INVISIBLE);
                    }else
                        onlineStatus.setVisibility(View.GONE);
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        };
        onlineStatusRef.addValueEventListener(onlineStatusListener);
    }
    
    public void bindMember(final ModelContact model, final Context context, final DatabaseReference ref)
    {
        TextView name = v.findViewById(R.id.name);
        ImageView photo = v.findViewById(R.id.image);
        TextView timer = v.findViewById(R.id.timer);
        ImageView statusOnline = v.findViewById(R.id.statusOnline);
        
        setUpOnlineStatus(model.getUid(), statusOnline);
        
        name.setText(model.getName());
        fetchImageFromUserInfo(photo, model.getUid(), context);
       
        if(model.getBlockStatus())
        {
            //show connection as blocked
            name.setTextColor(Color.GRAY);
           // about.setTextColor(Color.GRAY);
            //about.setText("Long press to unblock");
        }
        else
        if(!model.getBlockStatus())
        {
            name.setTextColor(context.getResources().getColor(R.color.read_color));
           // about.setText(model.getOneLine());
        }
    
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                if(model.getTemporaryContact())
                {
                    Intent i = new Intent(context, temporaryChatFullScreen.class);
                    i.putExtra(Constants.sendToUid, model.getUid());
                    i.putExtra(Constants.sendToName, model.getName());
                    context.startActivity(i);
                }
                else
                {
                    Intent i = new Intent(context, chatFullScreen.class);
                    i.putExtra(Constants.sendToUid, model.getUid());
                    i.putExtra(Constants.sendToName, model.getName());
                    context.startActivity(i);
                }
                
            }
        });
    
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(model.getUid(), ref, model.getBlockStatus(), context);
                return true;
            }
        });
        timer.setVisibility(View.GONE);
        /*if(model.getTimeStamp()!=null)
            setUpTimer(timer, (long) model.getTimeStamp().get(Constants.timeStamp), model.getUid());*/
    
    }
    
    private void fetchImageFromUserInfo(final ImageView photo, String uid, final Context context) {
        
        if(uid==null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(uid)
                .child("imageUrl");
        
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null)
                {
                    String url = dataSnapshot.getValue(String.class);
                    Glide.with(context).load(url).into(photo);
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
        
       
    }
    
    private void setUpTimer(final TextView timer, final long time, String uid) {
        
        //check if time exceeded 24 hours, if yes then remove contact;
       /* if(time<System.currentTimeMillis())
        {
            //remove contact
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.Messages)
                    .child(Constants.uid).child(Constants.contacts)
                    .child(uid);
            reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }*/
    
        final long[] startTime = {System.currentTimeMillis()- time};
        //adding 24 hours milliseconds with current time of milliseconds to make it 24 hours milliseconds.
       long milliseconds = 86400000 -(System.currentTimeMillis()- time) ; // 24 hours = 86400000 milliseconds
        Log.e(TAG, "Start time is "+milliseconds);
        CountDownTimer cdt = new CountDownTimer(milliseconds,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                
                
                startTime[0] = startTime[0] -1;
                Long serverUptimeSeconds =
                        (millisUntilFinished - startTime[0]) / 1000;
                
                // now you repalce txtViewHours,  txtViewMinutes, txtViewSeconds by your textView.
            
                String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                
                String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
            
                String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
               
                String time = hoursLeft+":"+minutesLeft+":"+secondsLeft;
                
                timer.setVisibility(View.VISIBLE);
                timer.setText(time);
            }
        
            @Override
            public void onFinish() {
                //execute deletion of contact
            }
        };
    
        cdt.start();
        
    }
    
    private void showDialog(final String uid, final DatabaseReference blockRef, Boolean blockStatus, final Context context) {
        
        final LayoutInflater factory = LayoutInflater.from(v.getContext());
        final View connectionSetting = factory.inflate(R.layout.connection_setting, null);
        
        final SweetAlertDialog mainDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Choose")
                .setConfirmText("Cancel")
                .setCustomView(connectionSetting);
        mainDialog.show();
    
        Button btn = mainDialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_4_dialogue));
        
        Button block = connectionSetting.findViewById(R.id.block);
        Button delete = connectionSetting.findViewById(R.id.delete);
        Button report = connectionSetting.findViewById(R.id.report);
        
        if (blockStatus!=null&&blockStatus) {
            block.setText("Unblock Connection");
            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //unblock user
                  SweetAlertDialog alertDialog =  new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Unblock connection?")
                            .setContentText("You will be able to send/receive further messages again")
                            .setConfirmButton("Proceed?", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                    //block connection
                                    sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.blockList)
                                            .child(Constants.uid)
                                            .child(uid);
                                    
                                    reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            //update the contact reference
                                            
                                            HashMap<String, Object> updateBlockStat = new HashMap<>();
                                            updateBlockStat.put("blockStatus", false);
                                            
                                            blockRef.updateChildren(updateBlockStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sweetAlertDialog.hide();
                                                    mainDialog.hide();
                                                }
                                            });
                                            
                                        }
                                    });
                                }
                            });
                            alertDialog.show();
                    Button btn = alertDialog.findViewById(R.id.confirm_button);
                    btn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_4_dialogue));
    
                    if(btn!=null)
                        btn.setTypeface(Utils.SFPRoLight(context));
                    TextView title = alertDialog.findViewById(R.id.title_text);
                    if(title!=null)
                        title.setTypeface(Utils.SFProRegular(context));
    
                    TextView contentText = alertDialog.findViewById(R.id.content_text);
                    if(contentText!=null)
                        contentText.setTypeface(Utils.SFPRoLight(context));
    
                }
            });
        } else {
            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  SweetAlertDialog alertDialog =   new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Block connection?")
                            .setContentText("You will not be able to send/receive further messages")
                            .setConfirmButton("Proceed?", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                    //block connection
                                    sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.blockList)
                                            .child(Constants.uid)
                                            .child(uid);
                                    
                                    reference.setValue(uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            //update the contact reference
                                            
                                            HashMap<String, Object> updateBlockStat = new HashMap<>();
                                            updateBlockStat.put("blockStatus", true);
                                            
                                            blockRef.updateChildren(updateBlockStat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sweetAlertDialog.hide();
                                                    mainDialog.hide();
                                                }
                                            });
                                            
                                        }
                                    });
                                }
                            });
                            alertDialog.show();
    
                    Button btn = alertDialog.findViewById(R.id.confirm_button);
                    btn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_4_dialogue));
                    if(btn!=null)
                        btn.setTypeface(Utils.SFPRoLight(context));
                    TextView title = alertDialog.findViewById(R.id.title_text);
                    if(title!=null)
                        title.setTypeface(Utils.SFProRegular(context));
    
                    TextView contentText = alertDialog.findViewById(R.id.content_text);
                    if(contentText!=null)
                        contentText.setTypeface(Utils.SFPRoLight(context));
                    
                }
            });
            
        }
        
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
              SweetAlertDialog alertDialog =   new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete Connection?")
                        .setContentText("Any message sent to you by the connection will not reach you anymore!")
                        .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.Messages)
                                        .child(Constants.uid).child(Constants.contacts)
                                        .child(uid);
                                reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //delete the messages also!
                                        
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.CHATS)
                                                .child(Constants.uid)
                                                .child(uid);
                                        reference1.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mainDialog.dismiss();
                                                sweetAlertDialog.dismiss();
                                            }
                                        });
                                       
                                    }
                                });
                                
                            }
                        }); alertDialog.show();
                Button btn = alertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_4_dialogue));
    
                if(btn!=null)
                    btn.setTypeface(Utils.SFPRoLight(context));
                TextView title = alertDialog.findViewById(R.id.title_text);
                if(title!=null)
                    title.setTypeface(Utils.SFProRegular(context));
    
                TextView contentText = alertDialog.findViewById(R.id.content_text);
                if(contentText!=null)
                    contentText.setTypeface(Utils.SFPRoLight(context));
            }
        });
        
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.reportedUsers)
                        .child(uid)
                        .child(Constants.uid)
                        .push();
                String report = uid + " was reported by "+Constants.uid;
                
                reference.setValue(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "User reported!", Toast.LENGTH_SHORT).show();
                    }
                });
                
            }
        });
        
    }
    private String getBiggerImage(String url) {
        String modifiedImageUrl = url;
        if (url != null) {
            /*
            update user's profile first
            */
            if (url.contains("/s96-c/"))
                modifiedImageUrl = url.replace("/s96-c/", "/s300-c/");
            
            /*else if (url.contains("facebook.com"))
                modifiedImageUrl = url.replace("picture", "picture?height=500");*/
        }
        
        return modifiedImageUrl;
        
    }
}
