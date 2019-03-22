package com.testlabic.datenearu.NewQuestionUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Activities.EditActivity;
import com.testlabic.datenearu.Models.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class EditQuestionTemplate extends AppCompatActivity implements View.OnClickListener {
    
    EditText question, optA, optB, optC, optD;
    String questionNumber;
    Button save;
    FloatingActionButton hint;
    private static final String TAG = EditQuestionTemplate.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_template);
        questionNumber = getIntent().getStringExtra(Constants.questionNumber);
        hint=findViewById(R.id.hint_question);
        
        question = findViewById(R.id.question);
        optA = findViewById(R.id.opt1);
        optB = findViewById(R.id.opt2);
        optC = findViewById(R.id.opt3);
        optD = findViewById(R.id.opt4);
        save = findViewById(R.id.save);

        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint();
            }
        });
        

        save.setOnClickListener(this);
    }
    public void hint()
    {
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference()
                .child("DatingHelp").child("DatingQuestions");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long random=(long) (Math.random() * dataSnapshot.getChildrenCount());
                String questionhint= dataSnapshot.child(String.valueOf(random)).getValue(String.class);
                questionhint=questionhint.replace("\""," ");
                question.setText(questionhint);
                optC.setText("Not Sure");
                optD.setText("Never thought about it");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    
    @Override
    public void onClick(View v) {
     
        if(!(question.getText().toString().matches("")||optA.getText().toString().matches("")||
                optB.getText().toString().matches("")||optC.getText().toString().matches("")
                ||optD.getText().toString().matches("")))
        {
            //save Data to database
            DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(Constants.uid)
                    .child("questions")
                    .child(questionNumber);
            
            String ques = question.getText().toString();
            String optAs = optA.getText().toString();
            String optBs = optB.getText().toString();
            String optCs = optC.getText().toString();
            String optDs = optD.getText().toString();
    
            ModelQuestion question = new ModelQuestion(ques, optAs, optBs, optCs, optDs, null);
            
            reference.setValue(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            });
            
        }
        else
            Toast.makeText(this, "Fill all five fields", Toast.LENGTH_SHORT).show();

    }
}
