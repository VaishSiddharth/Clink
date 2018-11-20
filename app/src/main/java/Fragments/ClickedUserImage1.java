package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClickedUserImage1 extends Fragment {
    
    private static final String TAG = ClickedUserImage1.class.getSimpleName();
    
    public ClickedUserImage1() {
        // Required empty public constructor
    }
    
    String imageUrl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_clicked_user_image1,container,false);
        ImageView imageView = view.findViewById(R.id.photos);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(Constants.imageUrl);
            Log.e(TAG, "Image url : "+imageUrl);
            Glide.with(getContext()).load
                    ( imageUrl).into(imageView);
    
        }
        return view;
    }
    
}
