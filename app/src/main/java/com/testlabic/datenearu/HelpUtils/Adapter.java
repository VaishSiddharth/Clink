package com.testlabic.datenearu.HelpUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.R;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    
    Context mContext;
    ArrayList<NearbyRestaurant> list;
    
    public Adapter(Context mContext, ArrayList<NearbyRestaurant> list ) {
        
        this.mContext = mContext;
        this.list = list;
        
    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public NearbyRestaurant getItem(int position) {
        return list.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
           convertView = LayoutInflater.from(mContext).inflate(R.layout.sample_nearbyplaces, null);
       
            NearbyRestaurant restaurant = getItem(position);
            
            TextView name = (TextView) convertView.findViewById(R.id.name);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            TextView vicinty = (TextView) convertView.findViewById(R.id.vicinity);
            RatingBar ratingBar = convertView.findViewById(R.id.ratings);
            name.setText(restaurant.getName());
            vicinty.setText(restaurant.getVicinity());
            Glide.with(mContext).load(restaurant.getIcon()).into(imageView);
            if(restaurant.getRating()!=null)
            ratingBar.setNumStars(Math.round(Float.parseFloat(restaurant.getRating())));
    
        return convertView;
    }
}
