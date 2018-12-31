package com.testlabic.datenearu.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClickedUserImage2 extends Fragment {
    
    public ClickedUserImage2() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_clicked_user_image2,container,false);
        ImageView imageView = view.findViewById(R.id.photos);
        Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/datenearu.appspot.com/o/1521861_425366384233501_1879099251_n.jpg?alt=media&token=c885f98a-b430-4a53-bacd-0698d1fb68fb")
                .into(imageView);
        return view;    }
    
}
