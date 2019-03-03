package com.testlabic.datenearu.NewQuestionUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.Models.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class EditQuestionTemplate extends AppCompatActivity implements View.OnClickListener {
    
    EditText question, optA, optB, optC, optD;
    String questionNumber;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_template);
        questionNumber = getIntent().getStringExtra(Constants.questionNumber);
        
        question = findViewById(R.id.question);
        optA = findViewById(R.id.opt1);
        optB = findViewById(R.id.opt2);
        optC = findViewById(R.id.opt3);
        optD = findViewById(R.id.opt4);
        save = findViewById(R.id.save);
        save.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
     
        if(!question.getText().toString().matches("")&&!question.getText().toString().matches("")&&
                !question.getText().toString().matches("")&&!question.getText().toString().matches(""))
        {
            //save Data to database
            DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                    .child(Constants.uid)
                    .child("questions")
                    .child(Integer.toString(0));
            
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
            Toast.makeText(this, "Fill all five", Toast.LENGTH_SHORT).show();
        
    }
}
