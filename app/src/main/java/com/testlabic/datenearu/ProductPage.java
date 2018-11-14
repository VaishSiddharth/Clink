package com.testlabic.datenearu;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import adapter.View_Pager_Adapter;
import me.relex.circleindicator.CircleIndicator;


public class ProductPage extends AppCompatActivity implements View.OnClickListener {


    ImageView f1;
    boolean first = true;


    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private View_Pager_Adapter view_pager_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);


        f1 = findViewById(R.id.f1);
        f1.setOnClickListener(this);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleindicator);
        view_pager_adapter = new View_Pager_Adapter(getSupportFragmentManager());
        viewPager.setAdapter(view_pager_adapter);
        circleIndicator.setViewPager(viewPager);
        view_pager_adapter.registerDataSetObserver(circleIndicator.getDataSetObserver());


    }


    @Override
    public void onClick(View view) {
        if (first) {
            f1.setBackgroundResource(R.drawable.ic_like_1);
            first = false;
        } else {
            f1.setBackgroundResource(R.drawable.ic_like_heart_outline);
            first = true;
        }
    }
}