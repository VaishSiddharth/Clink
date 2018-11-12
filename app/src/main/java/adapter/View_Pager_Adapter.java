package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
                Product_Fragment tab1 = new Product_Fragment();
                return tab1;
            case 1:
                Product_Fragment tab2 = new Product_Fragment();
                return  tab2;
            case 2:
                Product_Fragment tab3 = new Product_Fragment();
                return tab3;
            case 3:
                Product_Fragment tab4 = new Product_Fragment();
                return tab4;

                default:
                    return null;

        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
