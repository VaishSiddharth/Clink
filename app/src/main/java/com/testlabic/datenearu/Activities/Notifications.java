package com.testlabic.datenearu.Activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.Models.ModelNotification;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Notifications extends AppCompatActivity {
    
    private static final String TAG = Notifications.class.getSimpleName();
    FirebaseListAdapter<ModelNotification> adapter;
    GoogleProgressBar bar;
    DatabaseReference reference;
    int notifCount =-1;
    ListView listView;
    private ChildEventListener listener;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        bar = findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        listView = findViewById(R.id.listView);
        notifCount = getIntent().getIntExtra(Constants.notifCount, -1);
        
        /*
        Mark all notifications read and then display
         */
        MoveNotifToRead();
    }
    
    private void MoveNotifToRead() {
        
        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Notifications)
                .child(Constants.uid).child(Constants.unread);
        listener =  reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    ModelNotification notification = dataSnapshot.getValue(ModelNotification.class);
                    String pushKey = dataSnapshot.getKey();
                    if (notification != null) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.Notifications)
                                .child(Constants.uid).child(Constants.read).child(pushKey);
                        
                        reference.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                
                                dataSnapshot.getRef().setValue(null);
                                
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
        
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    bar.setVisibility(View.GONE);
                    
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.Notifications)
                            .child(Constants.uid)
                            .child(Constants.read);
                    FirebaseListOptions<ModelNotification> options = new FirebaseListOptions.Builder<ModelNotification>()
                            .setQuery(query, ModelNotification.class)
                            .setLayout(R.layout.sample_notif)
                            .build();
                    adapter = new FirebaseListAdapter<ModelNotification>(options) {
                        @Override
                        protected void populateView(View v, ModelNotification model, int position) {
                            
                          
                            
                            TextView txt = v.findViewById(R.id.txt);
                            TextView time = v.findViewById(R.id.time);
                            ImageView photo = v.findViewById(R.id.image);
                            if(position<notifCount)
                                txt.setTextColor(getResources().getColor(R.color.shade_black));
                            else
                                txt.setTextColor(getResources().getColor(R.color.read_color));
                            txt.setText(model.getMessage());
                            time.setText(SIMPLE_DATE_FORMAT.format(model.getTimeStamp()));
                            Glide.with(Notifications.this).load(model.getImageUrl()).into(photo);
                        }
                    };
                    listView.setAdapter(adapter);
                    adapter.startListening();
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        

        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        
        if(listener!=null)
            reference.removeEventListener(listener);
    }
}
