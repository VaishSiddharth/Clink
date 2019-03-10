package com.testlabic.datenearu.ChatUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.Adapters.ChatListAdapter;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.HelpUtils.Adapter;
import com.testlabic.datenearu.HelpUtils.ModelMainResults;
import com.testlabic.datenearu.Models.ApiClient;
import com.testlabic.datenearu.Models.ApiInterface;
import com.testlabic.datenearu.Models.ChatMessage;
import com.testlabic.datenearu.Models.LatLong;
import com.testlabic.datenearu.HelpUtils.NearbyRestaurant;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatFullScreen extends AppCompatActivity {
    
    private static final String TAG = chatFullScreen.class.getSimpleName();
    private static final String API_KEY = "AIzaSyBUNTTjP0e2dlmag-qAQR2H_CeLHpOApOo";
    private ListView chatListView;
    private TextView toolbarName;
    private String sendToUid;
    private String myUid;
    DatabaseReference reference;
    private ChildEventListener listener;
    private LinearLayout layout;
    private String sendersName;
    private GoogleProgressBar bar;
    private Boolean isOnlineForCurrentUser = false;
    private ChatListAdapter listAdapter;
    private ArrayList<DatabaseReference> msgReferenceList = new ArrayList<>();
    private ArrayList<DatabaseReference> msgReferenceListUsersCopy = new ArrayList<>();
    private String sendToName;
    ImageView imageView, help, emojis,back;
    boolean isTemporaryContact = false;
    private View rootView;
    private ArrayList<ChatMessage> messages = new ArrayList<>();
    private EmojiconEditText chatEditText1;
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                
                EditText editText = (EditText) v;
                
                if (v == chatEditText1) {
                   // if(isTemporaryContact)
                    //    deleteRef();
                    sendMessage(editText.getText().toString(), isOnlineForCurrentUser);
                }
                
                chatEditText1.setText("");
                
                return true;
            }
            return false;
            
        }
    };
    
    private void deleteRef() {
        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid)
                .child(Constants.contacts)
                .child(sendToUid)
                .child("timeStamp");
        ref.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isTemporaryContact = false;
            }
        });
    }
    
    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
            if (v == enterChatView1) {
                sendMessage(chatEditText1.getText().toString(), isOnlineForCurrentUser);
            }
            
            chatEditText1.setText("");
            
        }
    };
    
    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
        
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {
            
            } else {
                enterChatView1.setImageResource(R.drawable.ic_send);
                
            }
        }
        
        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                enterChatView1.setImageResource(R.drawable.ic_send);
            } else {
                enterChatView1.setImageResource(R.drawable.ic_send);
            }
        }
    };
    private ImageView enterChatView1;
    private DatabaseReference reference2;
    
    private void scrollMyListViewToBottom() {
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatListView.setSelection(listAdapter.getCount() - 1);
            }
        });
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_full_screen);
        
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        bar = findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setTitle("");
        
        Intent i = getIntent();
        
        sendToUid = i.getStringExtra(Constants.sendToUid);
        
        sendToName = i.getStringExtra(Constants.sendToName);
        
        myUid = FirebaseAuth.getInstance().getUid();
        
        if (myUid == null)
            return;
    
        checkBlockStatus();
        
        //checkIfItsTemporaryContact();
    
        fillMessageArray(sendToUid, myUid);
        
        toolbarName = findViewById(R.id.sendToName);
        
        toolbarName.setText(sendToName);
        
        imageView = findViewById(R.id.attach);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(chatFullScreen.this, "Under Development", Toast.LENGTH_SHORT).show();
            }
        });
        
        chatEditText1 = findViewById(R.id.chat_edit_text1);
        
        //hide chat box for blocked users and show snackbar
        layout = findViewById(R.id.layout);
        
        
        TextView emptyView = findViewById(R.id.emptyView);
        
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);
        
        chatListView = (ListView) findViewById(R.id.chat_list_view);
        
        help = findViewById(R.id.help);
        
        emojis = findViewById(R.id.emojis);
    
        rootView = findViewById(R.id.rootView);
    
        EmojIconActions emojIcon=new EmojIconActions(chatFullScreen.this ,rootView,chatEditText1,emojis,"#495C66","#DCE1E2","#E6EBEF");
        emojIcon.ShowEmojIcon();
        
        emojIcon.addEmojiconEditTextList(chatEditText1);
        
        chatListView.setEmptyView(emptyView);
        
        listAdapter = new ChatListAdapter(msgReferenceListUsersCopy, msgReferenceList, messages, this);
        
        scrollMyListViewToBottom();
        
        chatListView.setAdapter(listAdapter);
        
        chatEditText1.setOnKeyListener(keyListener);
        
        enterChatView1.setOnClickListener(clickListener);
        
        chatEditText1.addTextChangedListener(watcher1);
        
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
                DisplayHelpDialog();
                
            }
        });
        
        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                
                if (listAdapter != null) {
                    ChatMessage message = (ChatMessage) listAdapter.getItem(i);
                    /*
                    Get the reference and then delete the msg from your side
                     */
                    
                    showDeleteMsgBox(message);
                    
                }
                
                return false;
            }
        });
        
    }
    
    private void checkIfItsTemporaryContact() {
    
        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid)
                .child(Constants.contacts)
                .child(sendToUid)
                .child("timeStamp");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            //show temporay contact info
                            isTemporaryContact = true;
                            Toast.makeText(chatFullScreen.this, "This is a temporary contact, and will disappear if you don't reply in 24 hours!", Toast.LENGTH_SHORT).show();
                        }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
    }
    
    private void checkBlockStatus() {
        
        //Check if the other person blocked me or I blocked the other user
        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.blockList)
                .child(Constants.uid);
       listener =  reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.getValue(String.class)!=null)
                    {
                        String blockedUid = dataSnapshot.getValue(String.class);
                        if(sendToUid.equals(blockedUid))
                        {
                            //hide the chat edit text
                            layout.setVisibility(View.GONE);
                            Snackbar.make(layout, "You can't reply to this conversation", Snackbar.LENGTH_INDEFINITE).show();
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
      
       //check another reference
        reference2 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.blockList)
                .child(sendToUid);
        
        reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(String.class)!=null)
                {
                    String blockedUid = dataSnapshot.getValue(String.class);
                    if(Constants.uid.equals(blockedUid))
                    {
                        //hide the chat edit text
                        layout.setVisibility(View.GONE);
                        Snackbar.make(layout, "You can't reply to this conversation", Snackbar.LENGTH_INDEFINITE).show();
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
    
    private void DisplayHelpDialog() {
        final LayoutInflater factory = LayoutInflater.from(this);
        //final View helpDialog = factory.inflate(R.layout.help_layout, null);
    
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Help for you")
                .setContentText("Can't think of anything to ask your date? Tap to get some intersting questions!")
                .setConfirmText("5 drops")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.xPoints)
                                .child(Constants.uid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                                    if (modelSubscr != null) {
                                        int current = modelSubscr.getXPoints();
                                        if (current < Constants.dateQuestionPoints) {
                                            Toast.makeText(chatFullScreen.this, "You don't have enough points, buy now!", Toast.LENGTH_SHORT).show();
                                            BuyPoints();
                                        } else {
                        
                                            current -= Constants.dateQuestionPoints;
                                            HashMap<String, Object> updatePoints = new HashMap<>();
                                            updatePoints.put(Constants.xPoints, current);
                                            dataSnapshot.getRef().updateChildren(updatePoints);
                        
                                            final View questionListDialog = factory.inflate(R.layout.date_questions, null);
                        
                                            final SweetAlertDialog dialog = new SweetAlertDialog(chatFullScreen.this, SweetAlertDialog.NORMAL_TYPE)
                                                    // .setTitleText("Help for you")
                                                    // .setCancelText("Cancel")
                                                    .setCustomView(questionListDialog);
                        
                                            ListView listView = questionListDialog.findViewById(R.id.listView);
                        
                                            Query databaseRef = FirebaseDatabase.getInstance().getReference().child("DatingHelp").child("DatingQuestions");
                        
                                            FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                                                    .setLayout(R.layout.sample_date_question)
                                                    .setQuery(databaseRef, String.class)
                                                    .build();
                        
                                            final FirebaseListAdapter<String> adapter = new FirebaseListAdapter<String>(options) {
                                                @Override
                                                protected void populateView(View v, final String model, int position) {
                                                    TextView questionText = v.findViewById(R.id.questionText);
                                                    questionText.setText(model);
                                
                                                    questionText.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                           
                                                            String text = model.replace("\"", "");
                                                            chatEditText1.setText(text);
                                                            dialog.dismiss();
                                                            sweetAlertDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            };
                        
                                            adapter.startListening();
                        
                                            listView.setAdapter(adapter);
                                            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    adapter.stopListening();
                                                    sweetAlertDialog.dismiss();
                                
                                                }
                                            });
                                            dialog.show();
                        
                                        }
                                    }
                                }
                            }
        
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
            
                            }
                        });
    
                    }
                });
        
        
        //TextView dateQuestions = helpDialog.findViewById(R.id.dateQuestions);
        //TextView datePlaces = helpDialog.findViewById(R.id.place);
        
        /*datePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the mid point, then get restaurants near you!
    
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.xPoints)
                        .child(Constants.uid);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                            if (modelSubscr != null) {
                                int current = modelSubscr.getXPoints();
                                if (current < Constants.datePlacePoints) {
                                    Toast.makeText(chatFullScreen.this, "You don't have enough points, buy now!", Toast.LENGTH_SHORT).show();
                                    BuyPoints();
                                } else {
                        
                                    current -= Constants.datePlacePoints;
                                    HashMap<String, Object> updatePoints = new HashMap<>();
                                    updatePoints.put(Constants.xPoints, current);
                                    dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getPlaces(null);
                                            dialog.dismiss();
                                            fetchMidPoint();
                                        }
                                    });
                        
                                }
                            }
                        }
                    }
        
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
            
                    }
                });
                
               
            }
        });*/
        
        /*dateQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
    
                
                
            }
        });*/
        
        
        dialog.show();
    
        Button btn = dialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(chatFullScreen.this, R.drawable.button_4_dialogue));
        Button btn1 = dialog.findViewById(R.id.cancel_button);
        btn1.setBackground(ContextCompat.getDrawable(chatFullScreen.this, R.drawable.button_4_dialogue));
        
        btn1.setTypeface(Utils.SFPRoLight(this));
        btn.setTypeface(Utils.SFPRoLight(this));
        TextView title = dialog.findViewById(R.id.title_text);
        if(title!=null)
            title.setTypeface(Utils.SFProRegular(this));
    
        TextView contentText = dialog.findViewById(R.id.content_text);
        if(contentText!=null)
            contentText.setTypeface(Utils.SFPRoLight(this));
        
    }
    
    private void BuyPoints() {
            startActivity(new Intent(this, PurchasePacks.class));
    }
    
    public static LatLong midPoint(double lat1, double lon1, double lat2, double lon2){
        
        double dLon = Math.toRadians(lon2 - lon1);
        
        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);
        
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        
        //print out in degrees
        return new LatLong(Math.toDegrees(lon3), Math.toDegrees(lat3));
    }
    
    private void fetchMidPoint() {
        //get midpoint if both people's cordinates are avalable, else get one set of cordinates anyways
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(Constants.uid)
                .child(Constants.location);
    
    
        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(Constants.sendToUid)
                .child(Constants.location);
        
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                final LatLong latLong = dataSnapshot.getValue(LatLong.class);
                if(latLong!=null)
                {
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            
                            LatLong latLong1 = dataSnapshot.getValue(LatLong.class);
                            if (latLong1 != null) {
                                LatLong midPoint = midPoint(latLong.getLatitude(), latLong.getLongitude(), latLong1.getLatitude(), latLong1.getLongitude());
                                
                            }
                        }
    
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
        
                        }
                    });
                }
                
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
        
    }
    
    private void getPlaces(LatLong midPoint) {
    
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
            
        String location = "26.7824186,80.9285919";
        
        Call<ModelMainResults> call = apiService.getNearByResults(location, 5000, "restaurant", API_KEY);
        Log.e(TAG, "Reached getPlaces ");
        call.enqueue(new Callback<ModelMainResults>() {
            @Override
            public void onResponse(@NonNull Call<ModelMainResults> call, Response<ModelMainResults> response) {
                if (response.body() != null) {
    
                    ArrayList<NearbyRestaurant> restaurants = response.body().getResults();
                    
                    showRestaurantsInDialog(restaurants);
                }
            }
            @Override
            public void onFailure(Call<ModelMainResults> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
            
        });
    
    }
    
    private void showRestaurantsInDialog(final ArrayList<NearbyRestaurant> restaurants) {
    
        final LayoutInflater factory = LayoutInflater.from(this);
        final View helpDialog = factory.inflate(R.layout.restaurants_layout, null);
    
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                // .setTitleText("Help for you")
                .setCancelText("Cancel")
                .setCustomView(helpDialog);
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        
        //show adapter
    
        ListView listView = helpDialog.findViewById(R.id.listView);
        Adapter adapter = new Adapter(chatFullScreen.this, restaurants);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NearbyRestaurant restaurant = restaurants.get(position);
                    String text = "Wanna catch a bite at "+restaurant.getName()+"?";
                    chatEditText1.setText(text);
                    dialog.dismissWithAnimation();
            }
        });
        
        dialog.show();
        
    }
    
    private void showDeleteMsgBox(final ChatMessage message) {
        
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Delete message?");
        builder1.setCancelable(false);
        
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                
                deleteMessage(message);
            }
        });
        
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            
            }
        });
        
        AlertDialog alert11 = builder1.create();
        alert11.setIcon(R.drawable.ic_action_del);
        alert11.show();
        
    }
    
    private void deleteMessage(ChatMessage message) {
        /*
        Delete message by getting the reference
         */
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS)
                .child(uid)
                .child(sendToUid);
        if (message.getRefKey() != null) {
            reference = reference.child(message.getRefKey());
            //Log.e(TAG, "Reference about to be deleted is " + reference);
            reference.setValue(null);
            if (listAdapter != null) {
                if (sendToUid != null && myUid != null)
                    fillMessageArray(sendToUid, myUid);
                listAdapter.notifyDataSetChanged();
            }
        }
        
    }
    
    private void fillMessageArray(final String sendToUid, final String myUid) {
        
        /*
       Delete unreads
         */
        DatabaseReference delRef =  FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS + Constants.unread).child(Constants.uid + Constants.unread).child(sendToUid);
        delRef.setValue(null);
        
        if (sendToUid != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.CHATS).child(myUid).child(sendToUid);
            
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    messages.clear();
                    msgReferenceList.clear();
                    msgReferenceListUsersCopy.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatMessage message = snapshot.getValue(ChatMessage.class);
                        
                        if (message != null) {
                            messages.add(message);
                            String key = snapshot.getKey();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                    .child(Constants.CHATS).child(sendToUid).child(myUid)
                                    .child(key);
                            msgReferenceList.add(reference);
                            msgReferenceListUsersCopy.add(snapshot.getRef());
                        }
                        
                    }
                    
                    listAdapter.notifyDataSetChanged();
                    
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                
                }
            });
        }
        if(bar!=null)
            bar.setVisibility(View.GONE);
    }
    
    private void checkUsersStatus() {
          /*
        Before sending message check if user is on your chat screen and online, else move the messages to unread node!
         */
        //Querying database to check status
        
        DatabaseReference statusCheckRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.usersStatus)
                .child(sendToUid)
                .child(Constants.status + sendToUid);
        statusCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user is online for current user
                if (dataSnapshot.getValue(String.class) != null) {
                    isOnlineForCurrentUser = true;
                }
               // Log.e(TAG, "User is offline move to unread");
                isOnlineForCurrentUser = false;
                // send message to unread
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
    }
    
    private void sendMessage(final String messageText, boolean isOnlineOtherUser) {
        
        if (messageText.trim().length() == 0)
            return;
        
        HashMap<String, Object> timeStamp = new HashMap<>();
        
        timeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        
        String myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final DatabaseReference ref;
        DatabaseReference refUnread = null;
        ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS).child(sendToUid).child(myUid).push();
        if (!isOnlineOtherUser) {
            //increment the unread list
            
            refUnread = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.CHATS + Constants.unread).child(sendToUid + Constants.unread).child(myUid).push();
        }
        
        String refK = ref.getKey();
        
        final HashMap<String, Object> sentSuccessupdate = new HashMap<>();
        sentSuccessupdate.put(Constants.successfullySent, true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String imageUrl = String.valueOf(user.getPhotoUrl());
        final ChatMessage message = new ChatMessage(messageText, imageUrl, refK, myUid,
                sendToUid, false, new Date().getTime(), myName, sendToName, false);
        
        ref.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    ref.updateChildren(sentSuccessupdate);
                }
            }
        });
        if (refUnread != null) {
            refUnread.setValue(message);
        }
        
        final String pushKey = ref.getKey();
        
        final DatabaseReference myRef;
        if (pushKey != null) {
            myRef = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.CHATS).child(myUid).child(sendToUid).child(pushKey);
            
            
            myRef.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        myRef.updateChildren(sentSuccessupdate);
                    }
                }
            });
        }
        //message.setMessageStatus(Constants.DELIVERED);
        
    }
    
    private void updateStatus(String key, String status) {
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put(key, status);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.usersStatus)
                .child(Constants.uid);
        reference.updateChildren(updateStatus);
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        updateStatus(Constants.status + sendToUid, Constants.offline);
        
        
    }
    
    @Override
    protected void onDestroy() {
       
        if(listener!=null)
        {
            reference.removeEventListener(listener);
        }
        super.onDestroy();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        updateStatus(Constants.status + sendToUid, Constants.offline);
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(Constants.status, Constants.online);
        updateStatus(Constants.status + sendToUid, Constants.online);
        checkUsersStatus();
    }
}