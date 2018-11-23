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

import com.bumptech.glide.Glide;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.Models.ModelLastMessage;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import model.AllModel;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class LastMessageAdapter extends RecyclerView.Adapter<LastMessageAdapter.ViewHolder> {
    Context context;
    private ArrayList<ModelLastMessage> allModelArrayList;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    
    
    public LastMessageAdapter(Context context, ArrayList<ModelLastMessage> allModelArrayList) {
        this.context = context;
        this.allModelArrayList = allModelArrayList;
    }

    @NonNull
    @Override
    public LastMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LastMessageAdapter.ViewHolder holder, final int position) {
        
        final ModelLastMessage lastMessage = allModelArrayList.get(position);
        holder.name.setText(allModelArrayList.get(position).getName());
        holder.time.setText(SIMPLE_DATE_FORMAT.format(lastMessage.getTimeStamp()));
        holder.txt.setText(allModelArrayList.get(position).getLastMessage());
    
        Glide.with(context).load(lastMessage.getImageUrl()).into(holder.image);
        
       /* holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        if (position==0){
            holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        }else if (position==1){
            holder.linear.setBackgroundResource(R.drawable.rect_white_border);
        }else {
          holder.linear.setBackgroundResource(R.drawable.ract);
        }*/

        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                Intent i = new Intent(context, chatFullScreen.class);
    
                i.putExtra(Constants.sendToUid, lastMessage.getUid());
    
                i.putExtra(Constants.sendToName, lastMessage.getName());
    
                context.startActivity(i);
            }
        });
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
