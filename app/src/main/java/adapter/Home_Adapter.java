package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.testlabic.datenearu.R;

import java.util.ArrayList;

import model.Home_Model;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.ViewHolder> {
    Context context;
    private ArrayList<Home_Model> home_modelArrayList;

    public Home_Adapter(Context context, ArrayList<Home_Model> home_modelArrayList) {
        this.context = context;
        this.home_modelArrayList = home_modelArrayList;
    }

    @Override
    public Home_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_home,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Home_Adapter.ViewHolder holder, int position) {
        holder.bitmap1.setImageResource(home_modelArrayList.get(position).getBitmap1());
        holder.imagers.setImageResource(home_modelArrayList.get(position).getImagers());
        holder.textdji.setText(home_modelArrayList.get(position).getTextdji());
        holder.textprice.setText(home_modelArrayList.get(position).getTextprice());
    }

    @Override
    public int getItemCount() {
        return home_modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView  bitmap1,imagers;
        TextView textdji,textprice;

        public ViewHolder(View itemView) {
            super(itemView);
            bitmap1=itemView.findViewById(R.id.bitmap1);
            imagers=itemView.findViewById(R.id.imagers);
            textdji=itemView.findViewById(R.id.textdji);
            textprice=itemView.findViewById(R.id.textprice);


        }
    }
}
