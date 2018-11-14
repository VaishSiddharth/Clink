package fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.R;


/**
 * Created by wolfsoft4 on 21/8/18.
 */

public class Product_Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.productfragment,container,false);
        ImageView imageView = view.findViewById(R.id.photos);
        Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/datenearu.appspot.com/o/26991739_1209360915834040_19867583266844739_n.jpg?alt=media&token=1cdfbd9e-9faa-4cf9-89f6-b6972fdd3ffa")
                .into(imageView);
        return view;
    }
}
