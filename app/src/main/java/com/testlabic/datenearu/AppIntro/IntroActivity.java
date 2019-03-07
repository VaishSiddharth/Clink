package com.testlabic.datenearu.AppIntro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.testlabic.datenearu.Activities.SignIn;
import com.testlabic.datenearu.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);*/
        addSlide(SampleSlide.newInstance(R.layout.appintro_1));
        addSlide(SampleSlide.newInstance(R.layout.appintro_2));
        addSlide(SampleSlide.newInstance(R.layout.appintro_3));

        //addSlide(AppIntroFragment.newInstance("Clink", "New App Intro", R.drawable.qnaslider, Color.parseColor("#FF7A5BFE")));
        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Clink");
        sliderPage.setDescription("Working");
        sliderPage.setImageDrawable(R.drawable.final_app_logo1);
        sliderPage.setBgColor(Color.parseColor("#ffffff"));
        //addSlide(AppIntro2Fragment.newInstance(sliderPage));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#00979797"));
        //setSeparatorColor(Color.parseColor("#ffffff"));
        setIndicatorColor(Color.parseColor("#FF7A5BFE"),Color.parseColor("#33979797"));
        showStatusBar(false);
        showSeparator(false);
        setColorDoneText((Color.parseColor("#FF7A5BFE")));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setColorSkipButton((Color.parseColor("#FF7A5BFE")));


        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        startActivity(new Intent(IntroActivity.this, SignIn.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(IntroActivity.this, SignIn.class));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
        //Toast.makeText(getApplicationContext(),"This is problem",Toast.LENGTH_LONG).show();
    }
}