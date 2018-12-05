package Fragments;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.ChatUtils.ChatMessage;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelLastMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;

import adapter.LastMessageAdapter;

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
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_messages_list, container, false);
        recyclerview = (view).findViewById(R.id.recycler4);
        bar = view.findViewById(R.id.progress_bar);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        //downloadDataAndSetAdapter();
        bar.setVisibility(View.VISIBLE);
        return view;
        
    }
    
    private void downloadDataAndSetAdapter() {
        list = new ArrayList<>();
        list.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
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
                        
                        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
                        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
            
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            
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
        downloadDataAndSetAdapter();
    }
}
