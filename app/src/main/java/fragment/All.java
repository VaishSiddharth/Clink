package fragment;

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
import com.testlabic.datenearu.ChatUtils.ChatMessage;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelLastMessage;
import com.testlabic.datenearu.Models.ModelMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;

import adapter.LastMessageAdapter;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class All extends Fragment {
    
    private static final String TAG = All.class.getSimpleName();
    private LastMessageAdapter adapter;
    private RecyclerView recyclerview;
    private ArrayList<ModelLastMessage> list;
    
    Integer image[] = {R.drawable.profile, R.drawable.profile1, R.drawable.profile2, R.drawable.profile1, R.drawable.profile, R.drawable.profile2, R.drawable.profile1};
    String name[] = {"Keith Mills", "Hannah Chavez", "Ann Bates", "Martha Grant", "Alexander Scott", "Betty Lynch", "Debra Martin"};
    String time[] = {"24m ago", "40m ago", "1h ago", "2d ago", "4d ago", "5d ago", "1w ago"};
    String text[] = {"Hey, would you be interested in ...", "How about PayPal? Let me kn ...", "My final price would be 5k. Not m ...", "Let’s do this. Meet me at Starbucks", "Paid for the order", "Was great dealing with you. Thanks!", "Awesome. Thanks for this!"};
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_all, container, false);
        recyclerview = (view).findViewById(R.id.recycler4);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        downloadDataAndSetAdapter();
        return view;
        
    }
    
    private void downloadDataAndSetAdapter() {
        list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid)
                .child(Constants.contacts);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                if (dataSnapshot.getValue(ModelContact.class) != null) {
                    ModelContact contact = dataSnapshot.getValue(ModelContact.class);
                    if (contact != null) {
                        final String name = contact.getName();
                        final String imageUrl = contact.getImage();
                        final String uid = contact.getUid();
                        
                        /*
                        Now fetch the last message and time and setup adapter
                         */
    
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.CHATS)
                                .child(Constants.uid)
                                .child(uid)
                                ;
                        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
                        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String ref = dataSnapshot.getKey();
                                Log.e(TAG, "The ref is "+ref);
                                
                                for(DataSnapshot one: dataSnapshot.getChildren())
                                {
                                    if(one.getValue(ChatMessage.class)!=null) {
                                        if(one.getValue(ChatMessage.class)!=null)
                                        {
                                            String lastMessage = one.getValue(ChatMessage.class).getMessage();
                                            long timeStamp = one.getValue(ChatMessage.class).getSendingTime();
                                            if(lastMessage!=null) {
                                                ModelLastMessage message = new ModelLastMessage(name, imageUrl
                                                        , uid, lastMessage, timeStamp);
                                                list.add(message);
                                                adapter = new LastMessageAdapter(getActivity(), list);
                                                recyclerview.setAdapter(adapter);
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
}
