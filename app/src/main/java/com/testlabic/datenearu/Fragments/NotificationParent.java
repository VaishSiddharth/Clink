package com.testlabic.datenearu.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBarTab;
import com.testlabic.datenearu.Adapters.TablayoutAdapter;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationParent extends Fragment {
    
    private ViewPager viewPager;
    
    private TabLayout tabLayout;
    
    public NotificationParent() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification_parent, container, false);
      
        viewPager = rootView.findViewById(R.id.viewpager);
        tabLayout = rootView.findViewById(R.id.tablayout);

//        setCustomFontAndStyle(tabLayout, 0);
        
        tabLayout.addTab(tabLayout.newTab().setText("Notifications"));
        tabLayout.addTab( tabLayout.newTab().setText("Gifts"));
        
        addBadge();
    
        TablayoutAdapter adapter = new TablayoutAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        wrapTabIndicatorToTitle(tabLayout, 32, 32);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
        
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
            
                TextView tabTextView = new TextView(getActivity());
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
    
    
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            
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
        return rootView;
    }
    
    private void addBadge() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid).child(Constants.unread);
    
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            
                int giftUnopened = (int) dataSnapshot.getChildrenCount();
                //Log.e(TAG, giftUnopened+" yyy");
                if (giftUnopened > 0) {
                
                  String text = "Gifts"+"("+giftUnopened+")";
                  tabLayout.getTabAt(1).setText("HOO");
                   // Log.e("Test", "badge");
                    // Remove the badge when you're done with it.
                }
            
            }
        
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
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
        layoutParams.setMarginStart(start);
        layoutParams.setMarginEnd(end);
        layoutParams.leftMargin = start;
        layoutParams.rightMargin = end;
    }
    
    
    
}
