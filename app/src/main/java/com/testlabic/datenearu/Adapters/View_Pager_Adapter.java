package com.testlabic.datenearu.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.testlabic.datenearu.Utils.Constants;

import com.testlabic.datenearu.Fragments.ClickedUserImage1;
import com.testlabic.datenearu.Fragments.ClickedUserImage2;
import com.testlabic.datenearu.Fragments.multipleImages;

public class View_Pager_Adapter extends FragmentStatePagerAdapter {
  
    private String imageUrl1, imageUrl2, imageUrl3;
    private Boolean isBlur;
    
    public View_Pager_Adapter(FragmentManager fm, String imageUrl1, String imageUrl2, String imageUrl3, Boolean isBlur) {
        super(fm);
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.isBlur = isBlur;
    }
    
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                ClickedUserImage1 tab1 = new ClickedUserImage1();
                Bundle bundle= new Bundle();
                bundle.putString(Constants.imageUrl, imageUrl1);
                bundle.putBoolean(Constants.isBlur, isBlur);
                tab1.setArguments(bundle);
                return tab1;
                
            case 1:
                multipleImages tab2 = new multipleImages();
                Bundle bundle2= new Bundle();
                bundle2.putString(Constants.imageUrl, imageUrl2);
                bundle2.putBoolean(Constants.isBlur, isBlur);
                tab2.setArguments(bundle2);
                return  tab2;
                
            case 2:
                ClickedUserImage2 tab3 = new ClickedUserImage2();
                Bundle bundle3= new Bundle();
                bundle3.putString(Constants.imageUrl, imageUrl3);
                bundle3.putBoolean(Constants.isBlur, isBlur);
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
