package com.testlabic.datenearu.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.testlabic.datenearu.Fragments.AllMessagesList;
import com.testlabic.datenearu.Fragments.Connections;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class TablayoutAdapter extends FragmentStatePagerAdapter {

    int mnooftabs;


    public TablayoutAdapter(FragmentManager fm, int mnooftabs) {
        super(fm);
        this.mnooftabs = mnooftabs;
    }

    public TablayoutAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new AllMessagesList();
            case 1:
                return new Connections();
                

    default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
