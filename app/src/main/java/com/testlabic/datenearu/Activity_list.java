package com.testlabic.datenearu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import adapter.ListAdapter;
import model.ListModel;

public class Activity_list extends AppCompatActivity {

    private ListAdapter listAdapter;
    private RecyclerView recyclerview;
    private ArrayList<ListModel> listModelArrayList;

    String txt[]={"1.Splash_Screen_fliply","2.Login_fliply","3.Home_fliply","4.Product_Page_fliply",
    "5.Messages_fliply","6.Chat_fliply","7.Profile_fliply","8.Search-Results_fliply"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerview=findViewById(R.id.recyclerView1);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Activity_list.this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        listModelArrayList = new ArrayList<>();

        for (int i=0;i<txt.length;i++){

            ListModel listModel = new ListModel(txt[i]);

            listModelArrayList.add(listModel);

        }
        listAdapter = new ListAdapter(Activity_list.this,listModelArrayList);
        recyclerview.setAdapter(listAdapter);

    }
}
