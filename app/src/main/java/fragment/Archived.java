package fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.MessageUtils.MessagesAdapter;
import com.testlabic.datenearu.Models.ModelMessage;
import com.testlabic.datenearu.QuestionUtils.CardStackAdapter;
import com.testlabic.datenearu.QuestionUtils.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import adapter.AllAdapter;
import model.AllModel;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class Archived extends Fragment {
    private MessagesAdapter adapter;
    private RecyclerView recyclerview;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_all, container, false);
        
        recyclerview = (view).findViewById(R.id.recycler4);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        
        downloadRequests();
        return view;
        
    }
    
    private void downloadRequests() {
        final ArrayList<ModelMessage> requests = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid).child(Constants.requestMessages);
        
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                if (dataSnapshot.getValue() != null) {
                    ModelMessage item = dataSnapshot.getValue(ModelMessage.class);
                    if (item != null)
                        requests.add(item);
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
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                populate adapter...
                 */
                
                adapter = new MessagesAdapter(getContext(), requests);
                recyclerview.setAdapter(adapter);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
}
