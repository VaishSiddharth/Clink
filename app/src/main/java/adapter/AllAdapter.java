package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testlabic.datenearu.R;

import java.util.ArrayList;

import model.AllModel;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.ViewHolder> {
    Context context;
    private ArrayList<AllModel> allModelArrayList;

    public AllAdapter(Context context, ArrayList<AllModel> allModelArrayList) {
        this.context = context;
        this.allModelArrayList = allModelArrayList;
    }

    @NonNull
    @Override
    public AllAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllAdapter.ViewHolder holder, int position) {
        holder.image.setImageResource(allModelArrayList.get(position).getImage());
        holder.name.setText(allModelArrayList.get(position).getName());
        holder.time.setText(allModelArrayList.get(position).getTime());
        holder.txt.setText(allModelArrayList.get(position).getTxt());
        
        holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        if (position==0){
            holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        }else if (position==1){
            holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        }else {
          holder.linear.setBackgroundResource(R.drawable.ract);
        }



        if (position==0){
            holder.n1.setVisibility(View.VISIBLE);
        }else if (position==1) {
            holder.n1.setVisibility(View.VISIBLE);
        }else {
            holder.n1.setVisibility(View.GONE);
        }

        if (position==2){
            holder.n2.setVisibility(View.VISIBLE);
        }else if (position==3){
            holder.n3.setVisibility(View.VISIBLE);
        }else if (position==4){
            holder.n4.setVisibility(View.VISIBLE);
        }
        
    }

    @Override
    public int getItemCount() {
        return allModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,n2,n3,n4;
        TextView name,time,txt,n1;
        LinearLayout linear;


        public ViewHolder(View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            txt=itemView.findViewById(R.id.txt);
            linear =itemView.findViewById(R.id.linear);

            n1=itemView.findViewById(R.id.n1);
            n2=itemView.findViewById(R.id.n2);
            n3=itemView.findViewById(R.id.n3);
            n4=itemView.findViewById(R.id.n4);
            
        }
    }
}
