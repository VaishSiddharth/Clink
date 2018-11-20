package fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.Activities.Notifications;
import com.testlabic.datenearu.MessageUtils.MessagesAdapter;
import com.testlabic.datenearu.Models.ModelContact;
import com.testlabic.datenearu.Models.ModelMessage;
import com.testlabic.datenearu.QuestionUtils.CardStackAdapter;
import com.testlabic.datenearu.QuestionUtils.ModelQuestion;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import adapter.AllAdapter;
import model.AllModel;

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
                        
                        String facebookUserId = "";
                        // find the Facebook profile and get the user's id
                        for (UserInfo profile : currentUser.getProviderData()) {
                            // check if the provider id matches "facebook.com"
                            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                                facebookUserId = profile.getUid();
                            }
                        }
                        // construct the URL to the profile picture, with a custom height
                        // alternatively, use '?type=small|medium|large' instead of ?height=
                        String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
                        {
                            modifiedImageUrl = photoUrl;
                        }
                    }
                }
            }
        }
        return modifiedImageUrl;
        
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
    
    @Override
    public void onStop() {
        super.onStop();
    
        if(adapter!=null)
            adapter.stopListening();
    }
}
