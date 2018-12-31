package com.testlabic.datenearu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<ModelUser> displayArrayList;
    private String uid;
    public Home_Adapter(Context context, ArrayList<ModelUser> home_modelArrayList) {
        this.context = context;
        this.displayArrayList = home_modelArrayList;
    }
    
    @NonNull
    @Override
    public Home_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_home, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull final Home_Adapter.ViewHolder holder, final int position) {
        
        final ModelUser user = displayArrayList.get(holder.getAdapterPosition());
        
        Glide.with(context).load(getBiggerImage(user.getImageUrl())).into(holder.image);
        holder.name.setText(user.getUserName());
        holder.age.setText(String.valueOf(user.getAge() + " yr"));
        
        if(holder.getAdapterPosition()%2!=0)
            holder.upperSpace.setVisibility(View.VISIBLE);
        
        holder.completeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        
                //TODO: Remember to do respective changes..
                Intent i = new Intent(context, ClickedUser.class);
                i.putExtra(Constants.clickedUid, user.getUid());
                i.putExtra(Constants.imageUrl, user.getImageUrl());
                context.startActivity(i);
        
            }
        });
    }
    
    private String getBiggerImage(String url) {
        String modifiedImageUrl = null;
        if (url != null) {
        /*
        update user's profile first
         */
            
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                    if (user.getProviderId().equals("google.com")) {
                        
                        assert currentUser != null;
                        {
                            modifiedImageUrl = url.replace("/s96-c/", "/s300-c/");
                        }
                        
                    } else if (user.getProviderId().equals("facebook.com")) {
                        modifiedImageUrl = url;
                        if(!url.contains("/picture?height=500"))
                           modifiedImageUrl=  url.replace("/picture", "/picture?height=500");
                        
                    }
                }
            }
        }
        return modifiedImageUrl;
        
    }
    
    @Override
    public int getItemCount() {
        return displayArrayList.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView age, name;
        View completeItem, upperSpace, lowerSpace;
        
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            age = itemView.findViewById(R.id.age);
            name = itemView.findViewById(R.id.name);
            upperSpace = itemView.findViewById(R.id.upperSpace);
            lowerSpace = itemView.findViewById(R.id.lowerSpace);
            completeItem = itemView;
            
        }
    }
}
