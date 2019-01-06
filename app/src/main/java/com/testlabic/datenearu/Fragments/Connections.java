package com.testlabic.datenearu.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class Connections extends Fragment {
    private ListView listView;
    private FirebaseListAdapter<ModelContact> adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts, container, false);
        listView = view.findViewById(R.id.listView);
        setUpListView();
        return view;
        
    }
    
    private void setUpListView() {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid).child(Constants.contacts);

        FirebaseListOptions<ModelContact> options = new FirebaseListOptions.Builder<ModelContact>()
                .setQuery(ref, ModelContact.class)
                .setLayout(R.layout.sample_contacts)
                .build();
        adapter = new FirebaseListAdapter<ModelContact>(options) {
            @Override
            protected void populateView(View v, final ModelContact model, final int position) {
                TextView name = v.findViewById(R.id.name);
                ImageView photo = v.findViewById(R.id.image);
                TextView about = v.findViewById(R.id.about);
                if(model.getOneLine()!=null)
                    about.setText(model.getOneLine());
                name.setText(model.getName());
                Glide.with(getActivity()).load(getBiggerImage(model.getImage())).into(photo);
                if(model.getBlockStatus()!=null&&model.getBlockStatus())
                {
                    //show connection as blocked
                    name.setTextColor(Color.GRAY);
                    about.setTextColor(Color.GRAY);
                    about.setText("Long press to unblock");
                }
                else
                    if(model.getBlockStatus()!=null&&!model.getBlockStatus())
                    {
                        name.setTextColor(getResources().getColor(R.color.read_color));
                        about.setText(model.getOneLine());
                    }
                
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), chatFullScreen.class);
    
                        i.putExtra(Constants.sendToUid, model.getUid());
    
                        i.putExtra(Constants.sendToName, model.getName());
                        startActivity(i);
                    }
                });
                
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog(model.getUid(), getRef(position), model.getBlockStatus());
                        return true;
                    }
                });
                
            }
        };
        listView.setAdapter(adapter);
        
        
    }
    
    private void showDialog(final String uid, final DatabaseReference blockRef, Boolean blockStatus) {
    
        final LayoutInflater factory = LayoutInflater.from(getContext());
        final View connectionSetting = factory.inflate(R.layout.connection_setting, null);
    
        final SweetAlertDialog mainDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
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
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
    
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
                            Toast.makeText(getActivity(), "User reported!", Toast.LENGTH_SHORT).show();
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
    
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }
    
    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();
    }
}
