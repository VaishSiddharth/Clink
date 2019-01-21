package com.testlabic.datenearu.TransitionUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import org.w3c.dom.Text;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener {
    private static final String TAG = CommonFragment.class.getSimpleName();
    private ImageView imageView;
    private TextView name, age, oneLine;
    private RatingBar ratingBar;
    private String imageUrl, nameS, ageS , sendersUid, oneLineS, gender;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.image);
        Glide.with(CommonFragment.this).load(imageUrl).into(imageView);
        Log.e(TAG, "imageUrl is"+ imageUrl);
        name = rootView.findViewById(R.id.name);
        age = rootView.findViewById(R.id.age);
        oneLine = rootView.findViewById(R.id.oneLine);
        name.setText(nameS);
        age.setText("'"+ageS);
        //TODO: Add imageview for gender, next to age and show image likewise (as in
        

        if(oneLineS!=null)
            oneLine.setText(oneLineS);

        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
     
        Intent i = new Intent(getActivity(), ClickedUser.class);
        i.putExtra(Constants.clickedUid, sendersUid);
        i.putExtra(Constants.imageUrl, imageUrl);
        startActivity(i);
    }

    public void bindData(ModelUser user) {
        this.imageUrl = user.getImageUrl();
        this.nameS = user.getUserName();
        this.ageS = ""+user.getAge();
        this.sendersUid = user.getUid();
        this.oneLineS = user.getOneLine();
        this.gender = user.getGender();
    }
}
