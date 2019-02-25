package com.testlabic.datenearu.Adapters;

import com.testlabic.datenearu.Fragments.GiftsFragment;
import com.testlabic.datenearu.Fragments.NotificationFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import static com.facebook.share.model.AppGroupCreationContent.AppGroupPrivacy.Closed;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class TablayoutAdapter extends FragmentStatePagerAdapter {

    private int mnooftabs;


    public TablayoutAdapter(FragmentManager fm, int mnooftabs) {
        super(fm);
        this.mnooftabs = mnooftabs;
    }

    public TablayoutAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new NotificationFragment();
                
            case 1:
                return new GiftsFragment();
                
    default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return mnooftabs;
    }
}
