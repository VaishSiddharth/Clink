package com.testlabic.datenearu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import adapter.TablayoutAdapter_Home;

public class Home extends AppCompatActivity {

    ViewPager viewPager1;
    TabLayout tabLayout1;
    private FirebaseAuth mAuth;
    private Typeface mTypeface;
    private Typeface mTypefaceBold;
    private TextView changeLocation;
    private BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomBar = findViewById(R.id.bottomBar);
        mAuth = FirebaseAuth.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        viewPager1 = findViewById(R.id.viewpager1);
        tabLayout1 = findViewById(R.id.tablayout1);
        
        changeLocation = findViewById(R.id.changeLocation);
        
        /*
        refresh current location when change location is tapped.
         */
        
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        
                updateLocation();
            }
        });

        //setCustomFontAndStyle(tabLayout1, 0);
        tabLayout1.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayout1.addTab(tabLayout1.newTab().setText("All"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Cars"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Electronics"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Clothing"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Home"));


        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
        
                if(tabId == R.id.tab_message)
                {
                    // switch to messages fragment
                    
                }
            }
        });
        
//        Typeface mTypeface = Typeface.createFromAsset(getAssets(), "fonts/SF-Pro-Display-Bold.otf");
//        ViewGroup vg = (ViewGroup) tabLayout1.getChildAt(0);
//        int tabsCount = vg.getChildCount();
//        for (int j = 0; j < tabsCount; j++) {
//            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
//            int tabChildsCount = vgTab.getChildCount();
//            for (int i = 0; i < tabChildsCount; i++) {
//                View tabViewChild = vgTab.getChildAt(i);
//                if (tabViewChild instanceof TextView) {
//                    ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.BOLD);
//
//                }
//            }
//        }


        TablayoutAdapter_Home adapter = new TablayoutAdapter_Home(getSupportFragmentManager(), tabLayout1.getTabCount());
        viewPager1.setAdapter(adapter);

        wrapTabIndicatorToTitle(tabLayout1, 50, 50);
        viewPager1.setOffscreenPageLimit(5);
        viewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout1));

        for (int i = 0; i < tabLayout1.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout1.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(tab.getText());
                tabTextView.setTextColor(Color.parseColor("#acacac"));

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD);
                    tabTextView.setTextColor(Color.parseColor("#000000"));
                }

            }

        }



        tabLayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager1.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();

                text.setTextColor(Color.parseColor("#000000"));
                text.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTextColor(Color.parseColor("#acacac"));
                text.setTypeface(null, Typeface.NORMAL);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
    
    private void updateLocation() {
    
    
    }

//    private void setCustomFontAndStyle(TabLayout tabLayout, Integer position) {
//
//        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/SF-Pro-Display-Bold.otf");
//        mTypefaceBold = Typeface.createFromAsset(getAssets(), "fonts/SF-Pro-Display-Regular.otf");
//        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
//        int tabsCount = vg.getChildCount();
//        for (int j = 0; j < tabsCount; j++) {
//            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
//            int tabChildsCount = vgTab.getChildCount();
//            for (int i = 0; i < tabChildsCount; i++) {
//                View tabViewChild = vgTab.getChildAt(i);
//                if (tabViewChild instanceof TextView) {
//                    if (j == position) {
//                        ((TextView) tabViewChild).setTypeface(mTypefaceBold, Typeface.BOLD);
//                    } else {
//                        ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);
//                    }
//                }
//            }
//        }
//    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(Home.this, SignIn.class));
                    finish();
                }
            }
        });
        
    }
    
    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }

}










