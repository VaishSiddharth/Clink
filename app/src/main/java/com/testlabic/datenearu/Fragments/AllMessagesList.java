package com.testlabic.datenearu.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.ChatUtils.ChatMessage;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.Models.ConnectionViewHolder;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelLastMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.testlabic.datenearu.Adapters.LastMessageAdapter;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class AllMessagesList extends Fragment {
    
    private static final String TAG = AllMessagesList.class.getSimpleName();
    private LastMessageAdapter adapter;
    private GoogleProgressBar bar;
    private RecyclerView recyclerview;
    private ArrayList<ModelLastMessage> list;
    private String status= null;
    private RecyclerView cRecyclerView;
    private FirebaseRecyclerAdapter cAdapter;
    Query lastQuery;
    ValueEventListener valueEventListener;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_messages_list, container, false);
        recyclerview = (view).findViewById(R.id.recycler4);
        bar = view.findViewById(R.id.progress_bar);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
       // ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        cRecyclerView = view.findViewById(R.id.cRecylerView);
        //downloadDataAndSetAdapter();
        bar.setVisibility(View.VISIBLE);
        return view;
        
    }
    
    private void downloadDataAndSetAdapter() {
        list = new ArrayList<>();
        list.clear();
        
        final Query reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid)
                .child(Constants.contacts);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable final String s) {
                if(bar!=null)
                    bar.setVisibility(View.GONE);
                if (dataSnapshot.getValue(ModelContact.class) != null) {
                    ModelContact contact = dataSnapshot.getValue(ModelContact.class);
                    if (contact != null) {
                        final String name = contact.getName();
                        final String imageUrl = contact.getImage();
                        final String uid = contact.getUid();
                        
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.CHATS)
                                .child(Constants.uid)
                                .child(uid);
                         /*
                        Now fetch the last message and time and setup adapter
                         */
                        
                        lastQuery = databaseReference.orderByKey().limitToLast(1);
                       valueEventListener=  lastQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot one : dataSnapshot.getChildren()) {
                                    if (one.getValue(ChatMessage.class) != null) {
                                        if (one.getValue(ChatMessage.class) != null) {
                                            final String lastMessage = one.getValue(ChatMessage.class).getMessage();
                                            final long timeStamp = one.getValue(ChatMessage.class).getSendingTime();
                                            final Boolean isDelivered = one.getValue(ChatMessage.class).getMessageDelivered();
                                            final String sendersUid = one.getValue(ChatMessage.class).getSentFrom();
                                            final Boolean successfullySent = one.getValue(ChatMessage.class).getSuccessfullySent();
                                            if (lastMessage != null) {
                                                
                                                /*
                                                Check online status
                                                 */
                                                
                                                //Querying database to check status
                                                
                                                DatabaseReference statusCheckRef = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.usersStatus)
                                                        .child(uid)
                                                        .child(Constants.status);
                                                statusCheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        //check if user is online for current user
                                                        if (dataSnapshot.getValue(String.class) != null) {
                                                            String stat = dataSnapshot.getValue(String.class);
                                                            if (stat != null && stat.equals(Constants.online))
                                                                status = Constants.online;
                                                            else
                                                                status = Constants.offline;
                                                            
                                                        }
                                                        // Log.e(TAG, "Status for "+uid + " "+status);
                                                        ModelLastMessage message = new ModelLastMessage(name, imageUrl
                                                                , uid, lastMessage, timeStamp, isDelivered, sendersUid, status, successfullySent);
                                                        list.add(message);
                                                        //sort messages with time
                                                        Collections.sort(list, new Comparator<ModelLastMessage>() {
                                                            @Override
                                                            public int compare(ModelLastMessage v1, ModelLastMessage v2) {

                                                                long sub = v2.getTimeStamp() - (v1.getTimeStamp());
                                                                return (int) sub;
                                                            }
                                                        });
                                                        adapter = new LastMessageAdapter(getActivity(), list);
                                                        recyclerview.setAdapter(adapter);
                                                        
                                                    }
                                                    
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    
                                                    }
                                                });
                                                
                                            }
                                        }
                                        
                                    }
                                }
                            }
                            
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //Handle possible errors.
                            }
                        });
                        
                    }
                }
            }
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                if(adapter!=null) {
                    downloadDataAndSetAdapter();
                    //adapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(adapter!=null) {
                        adapter.notifyDataSetChanged();
                    downloadDataAndSetAdapter();
                   // adapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        setUpListView();
        if (cAdapter != null)
            cAdapter.startListening();
        downloadDataAndSetAdapter();
        
    }
    
    //code for connections
    
    private void setUpListView() {
        Log.e(TAG, "setting biew");
        
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid).child(Constants.contacts);
        
        FirebaseRecyclerOptions<ModelContact> options = new FirebaseRecyclerOptions.Builder<ModelContact>()
                .setQuery(ref, ModelContact.class)
                .build();
        cAdapter = new FirebaseRecyclerAdapter<ModelContact, ConnectionViewHolder>(options) {
            @NonNull
            @Override
            public ConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.sample_contacts, null, false);
                return new ConnectionViewHolder(view);
            }
    
            @Override
            protected void onBindViewHolder(@NonNull ConnectionViewHolder holder, int position, @NonNull ModelContact model) {
                    holder.bindMember(model, getActivity(), getRef(position));
            }
            
        };
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        cRecyclerView.setLayoutManager(layoutManager);
        cRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cRecyclerView.setAdapter(cAdapter);
        
        
    }
    
   
    
    @Override
    public void onStart() {
        super.onStart();
       
    }
    
    @Override
    public void onStop() {
        super.onStop();
        
        if (cAdapter != null)
            cAdapter.stopListening();
        
        if(valueEventListener!=null)
            lastQuery.removeEventListener(valueEventListener);
    }
}
