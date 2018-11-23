package com.testlabic.datenearu.ChatUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class chatFullScreen extends AppCompatActivity {
    
    private static final String TAG = chatFullScreen.class.getSimpleName();
    private ListView chatListView;
    private TextView toolbarName;
    private String sendToUid;
    private String myUid;
    private String sendersName;
    private ChatListAdapter listAdapter;
    private ArrayList<DatabaseReference> msgReferenceList = new ArrayList<>();
    private ArrayList<DatabaseReference> msgReferenceListUsersCopy = new ArrayList<>();
    private String sendToName;
    
    ImageView imageView;
    
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
                    sendMessage(editText.getText().toString());
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
                sendMessage(chatEditText1.getText().toString());
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
        
        chatListView.setEmptyView(emptyView);
        
        listAdapter = new ChatListAdapter(msgReferenceListUsersCopy, msgReferenceList, messages, this);
        
        scrollMyListViewToBottom();
        
        chatListView.setAdapter(listAdapter);
        
        chatEditText1.setOnKeyListener(keyListener);
        
        enterChatView1.setOnClickListener(clickListener);
        
        chatEditText1.addTextChangedListener(watcher1);
        
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
    }
    
    private void sendMessage(final String messageText) {
        
        if (messageText.trim().length() == 0)
            return;
        
        HashMap<String, Object> timeStamp = new HashMap<>();
        
        timeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        
        String myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS).child(sendToUid).child(myUid).push();
        String refK = ref.getKey();
    
        Boolean isAGroup = false;
        final ChatMessage message = new ChatMessage(messageText, null, refK, myUid,
                sendToUid, false, new Date().getTime(), isAGroup, myName, sendToName);
        
        ref.setValue(message);
        
        final String pushKey = ref.getKey();
        
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS).child(myUid).child(sendToUid).child(pushKey);
        
        myRef.setValue(message);
        //message.setMessageStatus(Constants.DELIVERED);
        
    }
}