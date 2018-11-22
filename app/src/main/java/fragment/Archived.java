package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.ChatUtils.chatFullScreen;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class Archived extends Fragment {
    private ListView listView;
    private FirebaseListAdapter<ModelContact> adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts, container, false);
        listView = view.findViewById(R.id.listView);
        setUpListView();
        return view;
        
    }
    
    private void setUpListView() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Messages)
                .child(Constants.uid).child(Constants.contacts);

        FirebaseListOptions<ModelContact> options = new FirebaseListOptions.Builder<ModelContact>()
                .setQuery(ref, ModelContact.class)
                .setLayout(R.layout.sample_contacts)
                .build();
        adapter = new FirebaseListAdapter<ModelContact>(options) {
            @Override
            protected void populateView(View v, ModelContact model, int position) {
                TextView name = v.findViewById(R.id.name);
                ImageView photo = v.findViewById(R.id.image);
                name.setText(model.getName());
                Glide.with(getActivity()).load(getBiggerImage(model.getImage())).into(photo);
            }
        };
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity(), chatFullScreen.class);

                i.putExtra(Constants.sendToUid, adapter.getItem(position).getUid());
                
                i.putExtra(Constants.sendToName, adapter.getItem(position).getName());
                
                startActivity(i);
            }
        });
    }
    
    private String getBiggerImage(String url) {
        String modifiedImageUrl = url;
        if (url != null) {
            /*
            update user's profile first
            */
            if (url.contains("/s96-c/"))
                modifiedImageUrl = url.replace("/s96-c/", "/s300-c/");
            
            /*else if (url.contains("facebook.com"))
                modifiedImageUrl = url.replace("picture", "picture?height=500");*/
        }

        return modifiedImageUrl;
        
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }
    
    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();
    }
}
