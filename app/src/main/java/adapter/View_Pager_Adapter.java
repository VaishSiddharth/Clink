package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import Fragments.ClickedUserImage1;
import Fragments.ClickedUserImage2;
import fragment.Product_Fragment;

public class View_Pager_Adapter extends FragmentStatePagerAdapter {
    public View_Pager_Adapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                ClickedUserImage1 tab1 = new ClickedUserImage1();
                return tab1;
            case 1:
                Product_Fragment tab2 = new Product_Fragment();
                return  tab2;
            case 2:
                ClickedUserImage2 tab3 = new ClickedUserImage2();
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
