package com.testlabic.datenearu.ChatUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class chatFullScreen extends AppCompatActivity {
    
    private static final String TAG = chatFullScreen.class.getSimpleName();
    private ListView chatListView;
    private TextView toolbarName;
    private String sendToUid;
    private String myUid;
    private String sendersName;
    private GoogleProgressBar bar;
    private Boolean isOnlineForCurrentUser = false;
    private ChatListAdapter listAdapter;
    private ArrayList<DatabaseReference> msgReferenceList = new ArrayList<>();
    private ArrayList<DatabaseReference> msgReferenceListUsersCopy = new ArrayList<>();
    private String sendToName;
    ImageView imageView, help;
    private ArrayList<ChatMessage> messages = new ArrayList<>();
    private EditText chatEditText1;
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                
                EditText editText = (EditText) v;
                
                if (v == chatEditText1) {
                    sendMessage(editText.getText().toString(), isOnlineForCurrentUser);
                }
                
                chatEditText1.setText("");
                
                return true;
            }
            return false;
            
        }
    };
    
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
        
        TextView emptyView = findViewById(R.id.emptyView);
        
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);
        
        chatListView = (ListView) findViewById(R.id.chat_list_view);
        
        help = findViewById(R.id.help);
        
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
    
    private void DisplayHelpDialog() {
        final LayoutInflater factory = LayoutInflater.from(this);
        final View helpDialog = factory.inflate(R.layout.help_layout, null);
    
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
        
        TextView dateQuestions = helpDialog.findViewById(R.id.dateQuestions);
        
        dateQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                        sweetAlertDialog.dismissWithAnimation();
                        
                    }
                });
                
                
                dialog.show();
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
                Log.e(TAG, "User is offline move to unread");
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