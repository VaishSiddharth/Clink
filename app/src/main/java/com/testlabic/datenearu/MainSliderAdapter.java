package com.testlabic.datenearu;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.bitmap1);
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.bitmap2);
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.bitmap3);
                break;
        }
    }
}