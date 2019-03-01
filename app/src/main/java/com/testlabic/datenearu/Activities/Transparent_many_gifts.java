package com.testlabic.datenearu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class Transparent_many_gifts extends Activity {

    ImageView imagePerson,gift,premium_bottle,royal_bottle,regular_bottle;
    TextView namePerson;
    private int count = 0;
    private FirebaseListAdapter<ModelGift> adapter;
    private ListView listView;
    private int giftUnopened;
    LinearLayout linearLayoutgiftsmany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_many_gifts);
        listView=findViewById(R.id.manygiftsList);
        int mWidth= this.getResources().getDisplayMetrics().widthPixels;
        int mHeight= this.getResources().getDisplayMetrics().heightPixels;
        KonfettiView viewKonfetti = findViewById(R.id.viewKonfetti);
        viewKonfetti.build()
                .addColors(getApplicationContext().getResources().getColor(R.color.appcolor),
                        getApplicationContext().getResources().getColor(R.color.yellow),
                        getApplicationContext().getResources().getColor(R.color.appcolor))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2500L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(8, 3))
                .setPosition(mWidth/2f,mHeight/7f)
                .burst(1000);

                /*.setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-100f, 1000f, -50f, -50f)
                .streamFor(300, 5000L);
        //listView.setAdapter(adapter);*/

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid).child(Constants.unread);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                giftUnopened = (int) dataSnapshot.getChildrenCount();
                giftUnopened += count;
                //Log.e(TAG, giftUnopened+" yyy");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelGift modelGift = snapshot.getValue(ModelGift.class);
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.Gifts)
                            .child(Constants.uid)
                            .child(Constants.unread).orderByChild("timeStamp");
                    FirebaseListOptions<ModelGift> options = new FirebaseListOptions.Builder<ModelGift>()
                            .setQuery(query, ModelGift.class)
                            .setLayout(R.layout.many_gifts_layout)
                            .build();
                    adapter=new FirebaseListAdapter<ModelGift>(options) {
                        @Override
                        protected void populateView(View v, final ModelGift model, final int position) {
                            imagePerson = v.findViewById(R.id.imageperson);
                            namePerson = v.findViewById(R.id.nameperson);
                            linearLayoutgiftsmany=v.findViewById(R.id.linearLayoutgiftsmany);
                            premium_bottle=v.findViewById(R.id.premium_bottle);
                            royal_bottle=v.findViewById(R.id.royal_bottle);
                            regular_bottle=v.findViewById(R.id.regular_bottle);
                            gift=v.findViewById(R.id.gift);
                            String winetype=model.getGiftType().toLowerCase().replaceAll("\\s+","");
                            int resID = getApplicationContext().getResources().getIdentifier(winetype , "drawable", getApplicationContext().getPackageName());
                            gift.setImageResource(resID);
                            Glide.with(Transparent_many_gifts.this)
                                    .load(model.getGiftSendersImageUrl()).into(imagePerson);
                            String message = model.getGiftSendersName()
                                    + " has sent you a gift. Tap to view profile!";
                            namePerson.setText(message);
                            linearLayoutgiftsmany.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Transparent_many_gifts.this, ClickedUser.class);
                                    i.putExtra(Constants.comingFromNotif, true);
                                    i.putExtra(Constants.clickedUid, model.getGiftSendersUid());
                                    startActivity(i);
                                }
                            });
                        }
                    };
                    listView.setAdapter(adapter);
                    adapter.startListening();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
