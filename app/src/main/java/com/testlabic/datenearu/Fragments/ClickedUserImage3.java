package com.testlabic.datenearu.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import jp.wasabeef.blurry.Blurry;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

/**
 * Created by wolfsoft4 on 21/8/18.
 */

public class ClickedUserImage3 extends Fragment {
    
    String imageUrl;
    private String TAG= ClickedUserImage3.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_clicked_user_image1,container,false);
        final ImageView imageView = view.findViewById(R.id.photos);
        if (getArguments().getString(Constants.imageUrl) != null) {
            imageUrl = getArguments().getString(Constants.imageUrl);
            boolean isBlur = getArguments().getBoolean(Constants.isBlur);
            //Log.e(TAG, "Image url : "+imageUrl);
            if(!isBlur)
                Glide.with(getContext()).load
                        ( imageUrl).into(imageView);
        
            else
            {
                /*RequestOptions myOptions = new RequestOptions()
                        .override(15, 15)
                        .diskCacheStrategy(DiskCacheStrategy.NONE);

                Glide.with(getContext())
                        .load( imageUrl)
                        .apply(myOptions)
                        .into(imageView);*/
                RequestOptions myOptions = new RequestOptions()
                        .override(15, 15)
                        .diskCacheStrategy(DiskCacheStrategy.NONE);

                Glide.with(this)
                        .load(imageUrl).apply(myOptions).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Blurry.with(imageView.getContext()).radius(25)
                                        .sampling(2).capture(imageView).into(imageView);
                            }
                        },10);
                        return false;
                    }
                }).into(imageView);
                imageView.setAlpha(0.7f);
            
            }
        
        }
        return view;
    }
}
