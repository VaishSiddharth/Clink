package Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelNotification;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private static final String TAG = NotificationFragment.class.getSimpleName();
    FirebaseListAdapter<ModelNotification> adapter;
    GoogleProgressBar bar;
    DatabaseReference reference;
    int notifCount =-1;
    SwipeMenuListView listView;
    private ChildEventListener listener;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    View rootView;
    public NotificationFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_notification, container, false);
        bar = rootView.findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        listView = rootView.findViewById(R.id.listView);
        //notifCount = getIntent().getIntExtra(Constants.notifCount, -1);
        
        /*
        Mark sample_last_message notifications read and then display
         */
        MoveNotifToRead();
        return rootView;
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
                    Glide.with(getActivity()).load(model.getImageUrl()).into(photo);
                }
            };
            listView.setAdapter(adapter);
            setSwipeMenu();
            adapter.startListening();
        }
        
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        
        }
    });
    
    
    
}
    
    private void setSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth((180));
                // set item title
                openItem.setTitle("Accept");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
                
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth((180));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_action_del);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        Toast.makeText(getActivity(), "Accepting request!", Toast.LENGTH_SHORT).show();
                        acceptRequest(adapter.getItem(position));
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.smoothOpenMenu(position);
                
            }
        });
        
    }
    
    private void acceptRequest(ModelNotification item) {
        if(item.getSendersUid()!=null)
        
        {
            final DatabaseReference ref  = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.Messages)
                    .child(Constants.uid)
                    .child(Constants.contacts)
                    .child(item.getSendersUid());
            
            DatabaseReference receiver = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(item.getSendersUid());
            receiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if(dataSnapshot.getValue()!=null)
                    {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid());
                            ref.setValue(contact);
                        }
                    }
                    
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
            
            /*
            
            Similarly setup contact for the other user
             */
            
            
            final DatabaseReference ref2  = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.Messages)
                    .child(item.getSendersUid())
                    .child(Constants.contacts)
                    .child(Constants.uid);
            
            DatabaseReference receiver2 = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(Constants.uid);
            receiver2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if(dataSnapshot.getValue()!=null)
                    {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (user != null) {
                            ModelContact contact = new ModelContact(user.getUserName(), user.getImageUrl(), user.getUid());
                            ref2.setValue(contact);
                        }
                    }
                    
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        
        if(listener!=null)
            reference.removeEventListener(listener);
    }
}
