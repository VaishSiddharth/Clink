package com.testlabic.datenearu.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.ChatUtils.ChatMessage;
import com.testlabic.datenearu.Models.ConnectionViewHolder;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelLastMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.testlabic.datenearu.Adapters.LastMessageAdapter;
import com.testlabic.datenearu.WaveDrawable;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class AllMessagesListFragment extends Fragment {
    
    private static final String TAG = AllMessagesListFragment.class.getSimpleName();
    private LastMessageAdapter adapter;
    private ImageView emptyView;
    private RecyclerView recyclerview;
    private ArrayList<ModelLastMessage> list;
    private String status= null;
    private RecyclerView cRecyclerView;
    private FirebaseRecyclerAdapter cAdapter;
    Query lastQuery;
    ValueEventListener valueEventListener;
    ChildEventListener childEventListener;
    Query reference;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_messages_list, container, false);
        recyclerview = (view).findViewById(R.id.recycler4);
        emptyView = view.findViewById(R.id.emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
       // ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        cRecyclerView = view.findViewById(R.id.cRecylerView);

        Drawable mWaveDrawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mWaveDrawable = new WaveDrawable(getContext().getDrawable(R.drawable.nomessagespng));
        }
        ((WaveDrawable) mWaveDrawable).setWaveAmplitude(30);
        ((WaveDrawable) mWaveDrawable).setWaveLength(580);
        ((WaveDrawable) mWaveDrawable).setWaveSpeed(12);
        //((WaveDrawable) mWaveDrawable).setLevel(20);
        ((WaveDrawable) mWaveDrawable).setIndeterminate(true);
        emptyView.setImageDrawable(mWaveDrawable);

        emptyView.setVisibility(View.VISIBLE);
        return view;
        
    }
    
    private void downloadDataAndSetAdapter() {
        list = new ArrayList<>();
        list.clear();
    
        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid)
                .child(Constants.contacts);
        childEventListener = reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable final String s) {
                
                if (dataSnapshot.getValue(ModelContact.class) != null) {
                    emptyView.setVisibility(View.GONE);
                    ModelContact contact = dataSnapshot.getValue(ModelContact.class);
                    if (contact != null) {
                        final String name = contact.getName();
                        final String imageUrl = contact.getImage();
                        final String uid = contact.getUid();
                        ModelLastMessage message = new ModelLastMessage(name, imageUrl
                                , uid, null, -1000000000, null, null, status, null);
                        list.add(message);
     
                    }}}
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
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
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
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
        
        if(childEventListener!=null)
            reference.removeEventListener(childEventListener);
    }
}
