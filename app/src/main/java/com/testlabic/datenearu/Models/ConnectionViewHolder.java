package com.testlabic.datenearu.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConnectionViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ConnectionViewHolder.class.getSimpleName();
    View v;
    public ConnectionViewHolder(@NonNull View itemView) {
        super(itemView);
        v = itemView;
    }
    
    public void bindMember(final ModelContact model, final Context context, final DatabaseReference ref)
    {
        TextView name = v.findViewById(R.id.name);
        ImageView photo = v.findViewById(R.id.image);
        TextView timer = v.findViewById(R.id.timer);
        name.setText(model.getName());
        Glide.with(context).load(getBiggerImage(model.getImage())).into(photo);
        if(model.getBlockStatus()!=null&&model.getBlockStatus())
        {
            //show connection as blocked
            name.setTextColor(Color.GRAY);
           // about.setTextColor(Color.GRAY);
            //about.setText("Long press to unblock");
        }
        else
        if(model.getBlockStatus()!=null&&!model.getBlockStatus())
        {
            name.setTextColor(context.getResources().getColor(R.color.read_color));
           // about.setText(model.getOneLine());
        }
    
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, chatFullScreen.class);
            
                i.putExtra(Constants.sendToUid, model.getUid());
            
                i.putExtra(Constants.sendToName, model.getName());
                context.startActivity(i);
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
        if(model.getTimeStamp()!=null)
            setUpTimer(timer, (long) model.getTimeStamp().get(Constants.timeStamp), model.getUid());
    
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
                .setCustomView(connectionSetting);
        mainDialog.show();
        
        Button block = connectionSetting.findViewById(R.id.block);
        Button delete = connectionSetting.findViewById(R.id.delete);
        Button report = connectionSetting.findViewById(R.id.report);
        
        if (blockStatus!=null&&blockStatus) {
            block.setText("Unblock Connection");
            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //unblock user
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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
                            })
                            .show();
                }
            });
        } else {
            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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
                            })
                            .show();
                }
            });
            
        }
        
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete Connection?")
                        .setContentText("Any message sent to you by the connection will not reach you anymore!")
                        .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.Messages)
                                        .child(Constants.uid).child(Constants.contacts)
                                        .child(uid);
                                reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mainDialog.dismiss();
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                                
                            }
                        }).show();
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
