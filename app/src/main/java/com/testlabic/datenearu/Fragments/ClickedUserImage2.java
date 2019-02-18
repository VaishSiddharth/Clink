package com.testlabic.datenearu.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import jp.wasabeef.blurry.Blurry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClickedUserImage2 extends Fragment {

    public ClickedUserImage2() {
        // Required empty public constructor
    }
    String imageUrl;
    private static final String TAG = ClickedUserImage2.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_clicked_user_image1,container,false);
        final ImageView imageView = view.findViewById(R.id.photos);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(Constants.imageUrl);
            boolean isBlur = getArguments().getBoolean(Constants.isBlur);
            //Log.e(TAG, "Image url : "+imageUrl);
            if(!isBlur)
                Glide.with(getContext()).load
                        ( imageUrl).into(imageView);

            else
            {
                Glide.with(getContext()).load
                        ( imageUrl).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        Blurry.with(getActivity())
                                .capture(imageView)
                                .into((ImageView) imageView);
                        return true;
                    }
                }).into(imageView);

            }

        }
        return view;
    }
}
