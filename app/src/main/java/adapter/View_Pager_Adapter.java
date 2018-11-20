package adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.testlabic.datenearu.Utils.Constants;

import Fragments.ClickedUserImage1;
import Fragments.ClickedUserImage2;
import fragment.Product_Fragment;

public class View_Pager_Adapter extends FragmentStatePagerAdapter {
  
    String imageUrl;
    
    public View_Pager_Adapter(FragmentManager fm, String imageUrl) {
        super(fm);
        this.imageUrl = imageUrl;
    }
    
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                ClickedUserImage1 tab1 = new ClickedUserImage1();
                Bundle bundle= new Bundle();
                bundle.putString(Constants.imageUrl, imageUrl);
                tab1.setArguments(bundle);
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
