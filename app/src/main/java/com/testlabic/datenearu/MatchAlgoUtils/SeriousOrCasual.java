package com.testlabic.datenearu.MatchAlgoUtils;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.testlabic.datenearu.NewUserSetupUtils.SeriousItems;
import com.testlabic.datenearu.R;

public class SeriousOrCasual extends AppCompatActivity {
    
    Button yes, no;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serious_or_casual);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeriousOrCasual.this, SeriousItems.class));
                Animatoo.animateShrink(SeriousOrCasual.this);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeriousOrCasual.this, CasualItems.class));
                Animatoo.animateZoom(SeriousOrCasual.this);
            }
        });
    }
}
