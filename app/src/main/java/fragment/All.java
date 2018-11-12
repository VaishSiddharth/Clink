package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testlabic.datenearu.R;

import java.util.ArrayList;

import adapter.AllAdapter;
import model.AllModel;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class All  extends Fragment{

    private AllAdapter allAdapter;
    private RecyclerView recyclerview;
    private ArrayList<AllModel> allModelArrayList;

    Integer image[]={R.drawable.profile,R.drawable.profile1,R.drawable.profile2,R.drawable.profile1,R.drawable.profile,R.drawable.profile2,R.drawable.profile1};
    String name[]={"Keith Mills","Hannah Chavez","Ann Bates","Martha Grant","Alexander Scott","Betty Lynch","Debra Martin"};
    String time[]={"24m ago","40m ago","1h ago","2d ago","4d ago","5d ago","1w ago"};
    String text[]={"Hey, would you be interested in ...","How about PayPal? Let me kn ...","My final price would be 5k. Not m ...","Let’s do this. Meet me at Starbucks","Paid for the order","Was great dealing with you. Thanks!","Awesome. Thanks for this!"};



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.recycler_all,container,false);


        recyclerview = (view).findViewById(R.id.recycler4);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        allModelArrayList = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            AllModel view1 = new AllModel(image[i],name[i],time[i],text[i]);
            allModelArrayList.add(view1);
        }
        allAdapter = new AllAdapter(getActivity(),allModelArrayList);
        recyclerview.setAdapter(allAdapter);
        return  view;

    }
}
