package com.testlabic.datenearu.Utils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.Models.ModelNoify;
import com.testlabic.datenearu.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationModActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_mod);
        final DatabaseReference reference_notification = FirebaseDatabase.getInstance().getReference().child("notification");
        final DatabaseReference reference_updates = FirebaseDatabase.getInstance().getReference().child("updates");

        final String eventId= getIntent().getStringExtra("eventId");
        String eventname= getIntent().getStringExtra("eventname");


        final EditText notification_title=findViewById(R.id.notification_title);
        final EditText notification_description=findViewById(R.id.notification_description);
        Button notification_submit=findViewById(R.id.notification_submit);

        notification_title.setText(eventname+" ANNOUNCEMENT");


        notification_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=notification_title.getText().toString();
                String description=notification_description.getText().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy hh:mm");
                String formattedDate = df.format(c.getTime());
                ModelNoify modelNoify=new ModelNoify(title,description);
                //Updates_details updates_details=new Updates_details(eventId,title,formattedDate,description,"");
                //reference_updates.push().setValue(updates_details);
                //reference_notification.child(eventId).push().setValue(ModelNoify);
                Toast.makeText(getApplicationContext(),"Uploaded Sucessfully",Toast.LENGTH_LONG).show();
                //finish();
            }
        });
    }

}