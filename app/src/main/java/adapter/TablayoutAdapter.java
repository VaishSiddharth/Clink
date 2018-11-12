package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragment.All;
import fragment.Archived;
import fragment.Closed;

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
                All tab1 = new All();
                return tab1;
            case 1:
                Archived tab2 = new Archived();
                return tab2;

            case 2:
                Closed tab3 = new Closed();
                return tab3;

    default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
