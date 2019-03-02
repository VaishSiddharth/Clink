package com.testlabic.datenearu.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;
import com.testlabic.datenearu.ClickedUser;
import com.testlabic.datenearu.Models.ModelGift;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class Transparent_many_gifts extends Activity {

    ImageView imagePerson, gift, premium_bottle, royal_bottle, regular_bottle;
    TextView namePerson;
    private int count = 0;
    private FirebaseListAdapter<ModelGift> adapter;
    private ListView listView;
    private int giftUnopened;

    DatabaseReference moveReadRef;
    ChildEventListener childEventListener;
    LinearLayout linearLayoutgiftsmany;
    private static final String TAG = Transparent_many_gifts.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_many_gifts);
        listView = findViewById(R.id.manygiftsList);
        final int mWidth = this.getResources().getDisplayMetrics().widthPixels;
        final int mHeight = this.getResources().getDisplayMetrics().heightPixels;
        final KonfettiView viewKonfetti = findViewById(R.id.viewKonfetti);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewKonfetti.build()
                        .addColors(getApplicationContext().getResources().getColor(R.color.appcolor),
                                getApplicationContext().getResources().getColor(R.color.yellow),
                                getApplicationContext().getResources().getColor(R.color.appcolor))
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(3000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(8, 3))
                        .setPosition(mWidth / 2f, mHeight / 7f)
                        .burst(600);
            }
        }, 500);


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
                    adapter = new FirebaseListAdapter<ModelGift>(options) {
                        @Override
                        protected void populateView(View v, final ModelGift model, final int position) {
                            imagePerson = v.findViewById(R.id.imageperson);
                            namePerson = v.findViewById(R.id.nameperson);
                            linearLayoutgiftsmany = v.findViewById(R.id.linearLayoutgiftsmany);
                            premium_bottle = v.findViewById(R.id.premium_bottle);
                            royal_bottle = v.findViewById(R.id.royal_bottle);
                            regular_bottle = v.findViewById(R.id.regular_bottle);
                            gift = v.findViewById(R.id.gift);
                            final RippleBackground rippleBackground = (RippleBackground) v.findViewById(R.id.content);
                            rippleBackground.startRippleAnimation();
                            String winetype = model.getGiftType().toLowerCase().replaceAll("\\s+", "");
                            int resID = getApplicationContext().getResources().getIdentifier(winetype, "drawable", getApplicationContext().getPackageName());
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
        moveGiftToRead();
    }

    private void removeListeners() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid).child(Constants.unread);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "count = " + dataSnapshot.getChildrenCount());
                dataSnapshot.getRef().setValue(null);
                moveReadRef.removeEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void moveGiftToRead() {
        moveReadRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.Gifts)
                .child(Constants.uid).child(Constants.unread);


        childEventListener = moveReadRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "Triggered");
                if (dataSnapshot.getValue() != null) {
                    ModelGift notification = dataSnapshot.getValue(ModelGift.class);
                    String pushKey = dataSnapshot.getKey();
                    if (notification != null && pushKey != null) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.Gifts)
                                .child(Constants.uid).child(Constants.read).child(pushKey);
                        //Log.e("Trans", "THe ref to push to reaad" + reference);
                        reference.setValue(notification);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "backpressed");
        super.onBackPressed();
        removeListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
