package com.testlabic.datenearu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;
import com.testlabic.datenearu.Activities.SignIn;

import java.util.ArrayList;

public class PaperOnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);

        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
                //Toast.makeText(getApplicationContext(), "Swiped from " + oldElementIndex + " to " + newElementIndex, Toast.LENGTH_SHORT).show();
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                // Probably here will be your exit action
                startActivity(new Intent(PaperOnboardingActivity.this, SignIn.class));
                //Toast.makeText(getApplicationContext(), "Swiped out right", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Just example data for Onboarding
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Attempt cheers",
                "Use quiz to get quality matches, there's now something you can do in a dating app",
                Color.parseColor("#ffffff"), R.drawable.qnaslider, R.drawable.ic_online_);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Blur your profile",
                "Get comfortable with the app, go incognito for first 7 days for free",
                Color.parseColor("#ffffff"), R.drawable.blurslider, R.drawable.ic_online_);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Send gifts",
                "Hmmm, can't get through the quiz? send gifts to get noticed",
                Color.parseColor("#ffffff"), R.drawable.giftslider, R.drawable.ic_online_);
        
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }
}
