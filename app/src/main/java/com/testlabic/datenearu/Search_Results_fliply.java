package com.testlabic.datenearu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import com.testlabic.datenearu.Adapters.Search_Results_Adapter;
import model.Search_Results_Fliply_Model;

public class Search_Results_fliply extends AppCompatActivity {

    private Search_Results_Adapter  search_results_adapter;
    private RecyclerView recyclerview;
    private ArrayList<Search_Results_Fliply_Model> search_results_fliply_modelArrayList;

    Integer bitmap5[]={};
    Integer imgrs[]={R.drawable.ic_rupee,R.drawable.ic_rupee,R.drawable.ic_rupee,R.drawable.ic_rupee};
    String txtdji[]={"DJI Phantom Pro","DJI Mavic Pro","DJI Phantom Pro","DJI Mavic Pro"};
    String txtprice[]={"1,49,000","45,000","1,49,000","45,000"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__results_fliply);


        recyclerview = findViewById(R.id.recycler2);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        search_results_fliply_modelArrayList = new ArrayList<>();

        for (int i = 0; i < bitmap5.length; i++) {
            Search_Results_Fliply_Model view = new Search_Results_Fliply_Model(bitmap5[i],imgrs[i],txtdji[i],txtprice[i]);
            search_results_fliply_modelArrayList.add(view);
        }
        search_results_adapter = new Search_Results_Adapter(Search_Results_fliply.this,search_results_fliply_modelArrayList);
        recyclerview.setAdapter(search_results_adapter);
    }
}
