package Fragments;

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
public class ClickedUserImage1 extends Fragment {
    
    public ClickedUserImage1() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_clicked_user_image1,container,false);
        ImageView imageView = view.findViewById(R.id.photos);
        Glide.with(getContext()).load
                ("https://firebasestorage.googleapis.com/v0/b/datenearu.appspot.com/o/17200980_962751787161622_6185167878117185347_n.jpg?alt=media&token=e5a815a0-e0e5-4c0b-aef2-2f038bfc668f")
                .into(imageView);
        return view;
    }
    
}
