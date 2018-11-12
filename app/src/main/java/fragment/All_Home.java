package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testlabic.datenearu.R;

import java.util.ArrayList;

import adapter.Home_Adapter;
import model.Home_Model;

/**
 * Created by wolfsoft4 on 24/9/18.
 */

public class All_Home extends Fragment {


    private Home_Adapter home_adapter;
    private RecyclerView recyclerview;
    private ArrayList<Home_Model> home_modelArrayList;

    Integer bitmap1[]={R.drawable.bitmap1,R.drawable.bitmap2,R.drawable.bitmap4,R.drawable.bitmap3};
    Integer imagers[]={R.drawable.ic_rupee,R.drawable.ic_rupee,R.drawable.ic_rupee,R.drawable.ic_rupee};
    String textdji[]={"IKEA Chair","Mustang 1964","Google Home","Google Home"};
    String textprice[]={"12,000","12,50,000","4,999","45,000"};





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.recycler_all_home,container,false);



        recyclerview = (view).findViewById(R.id.recycler5);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        home_modelArrayList = new ArrayList<>();

        for (int i = 0; i < bitmap1.length; i++) {
            Home_Model view1 = new Home_Model(bitmap1[i],imagers[i],textdji[i],textprice[i]);
            home_modelArrayList.add(view1);
        }
        home_adapter = new Home_Adapter(getActivity(),home_modelArrayList);
        recyclerview.setAdapter(home_adapter);


        return view;

    }
}
