package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragment.All_Home;
import fragment.Cars;
import fragment.Clothing;
import fragment.Electronics;

/**
 * Created by wolfsoft4 on 24/9/18.
 */

public class TablayoutAdapter_Home  extends FragmentStatePagerAdapter {

    int mnooftabs;

    public TablayoutAdapter_Home(FragmentManager fm, int mnooftabs) {
        super(fm);
        this.mnooftabs = mnooftabs;
    }

    public TablayoutAdapter_Home(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                All_Home tab1 = new All_Home();
                return tab1;
            case 1:
                Cars tab2 = new Cars();
                return tab2;

            case 2:
                Electronics tab3 = new Electronics();
                return tab3;

            case 3:
                Clothing tab4 = new Clothing();
                return tab4;
            case 4:
                Cars tab5 = new Cars();
                return tab5;


            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return 5;
    }
}
