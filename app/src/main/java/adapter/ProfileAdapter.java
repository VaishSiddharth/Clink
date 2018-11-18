package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testlabic.datenearu.Activities.Settings;
import com.testlabic.datenearu.ProfileUtils.AboutEditor;
import com.testlabic.datenearu.QuestionUtils.QuestionsActivity;
import com.testlabic.datenearu.R;

import java.util.ArrayList;

import model.ProfileModel;

/**
 * Created by wolfsoft4 on 20/9/18.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    Context context;
    private ArrayList<ProfileModel> profileModelArrayList;

    public ProfileAdapter(Context context, ArrayList<ProfileModel> profileModelArrayList) {
        this.context = context;
        this.profileModelArrayList = profileModelArrayList;
    }


    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {

        holder.inbox.setImageResource(profileModelArrayList.get(position).getInbox());
        holder.arrow.setImageResource(profileModelArrayList.get(position).getArrow());
        holder.txttrades.setText(profileModelArrayList.get(position).getTxttrades());
        holder.txthistory.setText(profileModelArrayList.get(position).getTxthistory());
        
        /*
        setting up clicks for items in profile section
         */
        
        if(position==0)
        {
            /*
            Code questions part here
             */
            
            holder.profileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, QuestionsActivity.class));
                }
            });
           
        }
        
        if(position==2)
        {
            /*
            Open about editor and make changes..
             */
            holder.profileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, AboutEditor.class));
                }
            });
            
        }
        
        if(position==3)
        {
            context.startActivity(new Intent(context, Settings.class));
        }

    }

    @Override
    public int getItemCount() {
        return profileModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView inbox,arrow;
        LinearLayout profileItem;
        TextView txttrades,txthistory;

        public ViewHolder(View itemView) {
            super(itemView);

            inbox=itemView.findViewById(R.id.inbox);
            arrow=itemView.findViewById(R.id.arrow);
            txttrades=itemView.findViewById(R.id.txttrades);
            txthistory=itemView.findViewById(R.id.txthistory);
            profileItem = itemView.findViewById(R.id.profileItem);
        }
    }
}
