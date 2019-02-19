package com.testlabic.datenearu.TransitionUtils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.android.publish.ads.banner.Banner;
import com.testlabic.datenearu.Activities.SignIn;
import com.testlabic.datenearu.ArchitectureUtils.ViewModels.CityLabelModel;
import com.testlabic.datenearu.ArchitectureUtils.ViewModels.PointLabelModel;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.LatLong;
import com.testlabic.datenearu.Models.ModelPrefs;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Levenshtein;
import com.testlabic.datenearu.Utils.locationUpdater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Aviharsh on 2016/9/18.
 */
public class pagerTransition extends Fragment {
    
    private static final String TAG = pagerTransition.class.getSimpleName();
    private TextView indicatorTv;
    private ViewPager viewPager;
    View rootView;
    HashMap<String, Object> updateMap = null;
    HashMap<String, Object> updateMinAge;
    HashMap<String, Object> updateMaxAge;
    private ShowcaseView showcaseView;
    private int counter = 0;
    String interestedGender;
    private List<CommonFragment> fragments = new ArrayList<>(); // 供ViewPager使用
    private ArrayList<ModelUser> displayArrayList;
    private TextView changeLocation;
    private FragmentStatePagerAdapter adapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageButton filterList;
    private CrystalRangeSeekbar age_seek;
    private SeekBar distance_seek;
    private LatLong currentUsersLatLong;
    private ModelPrefs prefs;
    private TextView points;
    private ImageView hideAd, bottle;
    DatabaseReference ref;
    String curUsersMatchSeq;
    private ChildEventListener childEventListener;
    
    public pagerTransition() {
        // Required empty public constructor
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
       
        rootView = inflater.inflate(R.layout.activity_pager_transition, viewPager, false);
        if (Constants.uid != null)
            fetchPreferences();
        sharedPreferences = getActivity().getSharedPreferences(Constants.userDetailsOff, MODE_PRIVATE);
        interestedGender = sharedPreferences.getString(Constants.userIntrGender, "male");
        curUsersMatchSeq = sharedPreferences.getString(Constants.matchAlgo, null);
        
        editor = sharedPreferences.edit();
        filterList = rootView.findViewById(R.id.filter);
        hideAd = rootView.findViewById(R.id.hideAd);
        changeLocation = rootView.findViewById(R.id.changeLocation);
        points = rootView.findViewById(R.id.points);
        bottle = rootView.findViewById(R.id.fill_bottle);
        Constants.uid = FirebaseAuth.getInstance().getUid();
        if (Constants.uid != null)
            setUpPoints();
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), locationUpdater.class));
            }
        });
        downloadList();
        
        if (!sharedPreferences.getBoolean(Constants.shownShowCaser, false))
            setupShowCaser();
        
        filterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        
        hideAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Banner banner = rootView.findViewById(R.id.startAppBanner);
                banner.hideBanner();
            }
        });
        setShowcaseView();
        
        //setupCityLabelLocation
    
        // Obtain a new or prior instance of HotStockViewModel from the
        // ViewModelProviders utility class.
        CityLabelModel viewModel = ViewModelProviders.of(this).get(CityLabelModel.class);
    
        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();
        
        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null)
                    {
                        Log.e(TAG, "Setting value using architecture "+ dataSnapshot.getValue());
                        if (dataSnapshot.getValue() != null) {
                            String value = dataSnapshot.getValue(String.class);
                            changeLocation.setText(value);
                        }
                    }
            }
        });
    
        return rootView;
        
    }
    
    public void setShowcaseView() {
        //showcase view
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        Log.e(TAG, "Showcase working i " + isFirstRun);
        //Log.e(TAG, "Showcase working " + isFirstRun);
        if (isFirstRun) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        showcaseView = new ShowcaseView.Builder(getActivity())
                                .setTarget(new ViewTarget(filterList))
                                .setStyle(R.style.CustomShowcaseTheme2)
                                .setContentTitle("PLANNER")
                                .hideOnTouchOutside()
                                .setContentText("SET REMINDER FOR EVENT")
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        switch (counter) {
                                            case 0:
                                                showcaseView.setShowcase(new ViewTarget(changeLocation), true);
                                                showcaseView.setContentTitle("LOCATION");
                                                showcaseView.setContentText("FIND THE WAY TO REACH THE VENUE");
                                                break;
                                            
                                            case 1:
                                                showcaseView.setShowcase(new ViewTarget(viewPager), true);
                                                showcaseView.setContentTitle("OPEN CARD");
                                                showcaseView.setContentText("TAP TO SEE THE DETAILS OF THE EVENT");
                                                break;
                                            
                                            case 2:
                                                showcaseView.hide();
                                                setAlpha(1.0f, filterList, changeLocation, viewPager);
                                                break;
                                        }
                                        counter++;
                                    }
                                })
                                .build();
                    }
                }
            }, 2000);
            // runOnce = true;
        }
        // Code to run once
        SharedPreferences.Editor editor = wmbPreference.edit();
        editor.putBoolean("FIRSTRUN", false).apply();
        
    }
    
    private void setAlpha(float alpha, View... views) {
        for (View view : views) {
            view.setAlpha(alpha);
        }
    }
    
    private void setUpPoints() {
        PointLabelModel viewModel =  ViewModelProviders.of(this).get(PointLabelModel.class);
        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();
        
        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                ModelSubscr modelSubscr = null;
                if (dataSnapshot != null) {
                    modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                }
                if (modelSubscr != null) {
                    int current = modelSubscr.getXPoints();
                    String set = String.valueOf(current) + " drops";
                    points.setText(set);
        
                    //code for bottle image
                    if(current>=0&&current<=50){
                        bottle.setImageResource(R.drawable.bottle_1);
                    }
                    else if(current>50&&current<=100){
                        bottle.setImageResource(R.drawable.bottle_2);
                    }
                    else if(current>100&&current<=150){
                        bottle.setImageResource(R.drawable.bottle_3);
                    }
                    else if(current>150&&current<=200){
                        bottle.setImageResource(R.drawable.bottle_5);
                    }
                    else if(current>250&&current<=300){
                        bottle.setImageResource(R.drawable.bottle_5);
                    }
                    else if(current>350&&current<=400){
                        bottle.setImageResource(R.drawable.bottle_6);
                    }
                    else if(current>450&&current<=500){
                        bottle.setImageResource(R.drawable.bottle_7);
                    }
                    else if(current>550&&current<=600){
                        bottle.setImageResource(R.drawable.bottle_8);
                    }
                    else if(current>650&&current<=700){
                        bottle.setImageResource(R.drawable.bottle_9);
                    }
                    else if(current>750&&current<=800){
                        bottle.setImageResource(R.drawable.bottle_10);
                    }
                    else if(current>850&&current<=900){
                        bottle.setImageResource(R.drawable.bottle_11);
                    }
                    else if(current>950&&current<=1000){
                        bottle.setImageResource(R.drawable.bottle_12);
                    }
                    else if(current>1050&&current<=1100){
                        bottle.setImageResource(R.drawable.bottle_13);
                    }
                    else if(current>1150&&current<=1200){
                        bottle.setImageResource(R.drawable.bottle_14);
                    }
                    else if(current>1250&&current<=1300){
                        bottle.setImageResource(R.drawable.bottle_15);
                    }
                    else{
                        bottle.setImageResource(R.drawable.bottle_16);
                    }
        
                    points.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                
                            startActivity(new Intent(getActivity(), PurchasePacks.class));
                        }
                    });
                }
            }
        });
        
    }
    
    private void fetchPreferences() {
        DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userPreferences)
                .child(Constants.uid);
        refPrefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    prefs = dataSnapshot.getValue(ModelPrefs.class);
                    editor.putString(Constants.userIntrGender, prefs.getPreferedGender()).apply();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        // get lat long
        DatabaseReference latLong = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo)
                .child(Constants.uid)
                .child(Constants.location);
        latLong.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    currentUsersLatLong = dataSnapshot.getValue(LatLong.class);
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        
    }
    
    private void showFilterDialog() {
        
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View filterDialogView = factory.inflate(R.layout.filter_dialog, null);
        /*final AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
        deleteDialog.setView(filterDialogView);
        deleteDialog.show();*/
        
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Filters")
                .setConfirmText("Save")
                .setCustomView(filterDialogView);
        
        RadioGroup genderGroup = filterDialogView.findViewById(R.id.genderRadio);
        RadioButton maleRButton = filterDialogView.findViewById(R.id.maleRadio);
        RadioButton femaleRButton = filterDialogView.findViewById(R.id.femaleRadio);
        final TextView distance = filterDialogView.findViewById(R.id.distance_value);
        
        if (prefs != null) {
            if (prefs.getPreferedGender().equals(Constants.male)) {
                maleRButton.setChecked(true);
                femaleRButton.setChecked(false);
            } else {
                femaleRButton.setChecked(true);
                maleRButton.setChecked(false);
            }
        }
        
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadio) {
                    //update preferences
                    DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.userPreferences)
                            .child(Constants.uid);
                    
                    HashMap<String, Object> updateMap = new HashMap<>();
                    updateMap.put(Constants.preferedGender, Constants.male);
                    // Toast.makeText(getActivity(), "Getting new results", Toast.LENGTH_SHORT).show();
                    refPrefs.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            
                            editor.putString(Constants.userIntrGender, Constants.male).apply();
                            downloadList();
                        }
                    });
                    
                } else {
                    //update preferences
                    DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.userPreferences)
                            .child(Constants.uid);
                    
                    HashMap<String, Object> updateMap = new HashMap<>();
                    updateMap.put(Constants.preferedGender, Constants.female);
                    // Toast.makeText(getActivity(), "Getting new results", Toast.LENGTH_SHORT).show();
                    refPrefs.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            editor.putString(Constants.userIntrGender, Constants.female).apply();
                            downloadList();
                        }
                    });
                }
            }
        });
        age_seek = filterDialogView.findViewById(R.id.age_seekbar);
        distance_seek = filterDialogView.findViewById(R.id.distance_seekbar);
        
        final TextView tvMin = filterDialogView.findViewById(R.id.minAge);
        final TextView tvMax = filterDialogView.findViewById(R.id.maxAge);
        final TextView tvAgeRange = filterDialogView.findViewById(R.id.ageRange);
        // set listener
        age_seek.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));
            }
        });

// set final value listener
        if (prefs != null) {
            int maxAge = prefs.getMaxAge();
            int minAge = prefs.getMinAge();
            String age_range = "(" + minAge + " - " + maxAge + " yrs)";
            tvAgeRange.setText(age_range);
            tvMin.setText("18");
            tvMax.setText("50");
            
            age_seek.setMaxStartValue(maxAge).apply();
            age_seek.setMinStartValue(minAge).apply();
        }
        
        age_seek.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                
                updateMinAge = new HashMap<>();
                updateMinAge.put("minAge", minValue.intValue());
                
                updateMaxAge = new HashMap<>();
                updateMaxAge.put("maxAge", maxValue.intValue());
                
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                
                //update the preferences
                
                final DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userPreferences)
                        .child(Constants.uid);
                
                refPrefs.updateChildren(updateMaxAge);
                refPrefs.updateChildren(updateMinAge);
            }
        });
        
        double dist = prefs != null ? prefs.getDistanceLimit() : 0.0;
        distance.setText(String.valueOf((int) dist) + " km");
        distance_seek.setProgress((int) dist);
        
        distance_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                
                distance.setText(String.valueOf((int) (double) progress + 10) + " km");
                // update the database
                
                updateMap = new HashMap<>();
                updateMap.put("distanceLimit", (double) progress + 10);
                
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                if (updateMap != null) {
                    final DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.userPreferences)
                            .child(Constants.uid);
                    refPrefs.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //sweetAlertDialog.dismissWithAnimation();
                            downloadList();
                            
                        }
                    });
                } else
                    downloadList();
                
            }
        });
        dialog.show();
        Button btn = dialog.findViewById(R.id.confirm_button);
        btn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_4_dialogue));
        
    }
    
    private void setupShowCaser() {
        new ShowcaseView.Builder(getActivity())
                //.withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(changeLocation))
                .hideOnTouchOutside()
                .setContentTitle("Your current city")
                .setContentText("To change your city tap here, and then you can see profiles from your city")
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        //onHiddenFirstShowcase();
                    }
                })
                .withHoloShowcase()
                .build();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.shownShowCaser, true).apply();
    }
    
    private void fillViewPager(final ArrayList<ModelUser> displayArrayList) {
        
        indicatorTv = (TextView) rootView.findViewById(R.id.indicator_tv);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        
        viewPager.setPageTransformer(false, new CustPagerTransformer(getActivity()));
        
        for (int i = 0; i < displayArrayList.size(); i++) {
            fragments.add(new CommonFragment());
        }
        
        if (getActivity() != null) {
            adapter = new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    CommonFragment fragment = fragments.get(position);
                    fragment.bindData(displayArrayList.get(position));
                    return fragment;
                }
                
                @Override
                public int getCount() {
                    return displayArrayList.size();
                }
            };
            
            viewPager.setAdapter(adapter);
            
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                
                @Override
                public void onPageSelected(int position) {
                    updateIndicatorTv();
                }
                
                @Override
                public void onPageScrollStateChanged(int state) {
                
                }
            });
            updateIndicatorTv();
        }
        
    }
    
    private void downloadList() {
        
        displayArrayList = new ArrayList<>();
        displayArrayList.clear();
        String city = sharedPreferences.getString(Constants.cityLabel, "Lucknow_Uttar Pradesh_India");
        Log.e(TAG, "current city is "+city);
        interestedGender = sharedPreferences.getString(Constants.userIntrGender, "male");
        ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels).child(city).child(interestedGender);
        
        //Log.e(TAG, "download list called "+ ref);
        childEventListener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    ModelUser item = dataSnapshot.getValue(ModelUser.class);
                    if (item != null) {
                        filterList(item);
                    }
                }
            }
            
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "The child changed");
                downloadList();
            }
            
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                downloadList();
            }
            
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //populate adapter
                
                //sort the arraylist here using LevenshteinDistance algorithm
                
                //Log.e(TAG, "Called");
                sortWithMatch();
                Collections.sort(displayArrayList, new Comparator<ModelUser>() {
                    @Override
                    public int compare(ModelUser v1, ModelUser v2) {
                        double sub = v1.getMatchIndex() - (v2.getMatchIndex());
                        //Log.e(TAG, "The sub is "+ v1.getUserName() + " second name "+v2.getUserName()+" sum is " +sub);
                        return (int) sub;
                    }
                });
                fillViewPager(displayArrayList);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void sortWithMatch() {
        
        final Levenshtein l = new Levenshtein();
        
        for (final ModelUser user : displayArrayList) {
            if (curUsersMatchSeq == null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userInfo).child(Constants.uid).child("matchAlgo");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            curUsersMatchSeq = dataSnapshot.getValue(String.class);
                            if (user.getMatchAlgo() != null) {
                                editor.putString(Constants.matchAlgo, curUsersMatchSeq).apply();
                                double s = l.distance(curUsersMatchSeq, user.getMatchAlgo());
                                user.setMatchIndex(s);
                               // downloadList();
                            }
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    
                    }
                });
            } else {
                Log.e(TAG, "The match seq is "+ curUsersMatchSeq);
                if (user.getMatchAlgo() != null) {
                    double s = l.distance(curUsersMatchSeq, user.getMatchAlgo());
                    user.setMatchIndex(s);
                    // Log.e(TAG, "The match index is "+ s);
                }
            }
        }
        
    }
    
    private void filterList(ModelUser item) {
        //pass the item through the filters and then add them to the list for adaper;
        //1. Age filter
        if (prefs != null && item.getNumeralAge() >= prefs.getMinAge() && item.getNumeralAge() <= prefs.getMaxAge()
                && distanceBetweenThem(item.getLocation()) <= prefs.getDistanceLimit()) {
            displayArrayList.add(item);
        }
    }
    
    private double distanceBetweenThem(LatLong location) {
        
        //  if(currentUsersLatLong!=null)
        // return distance(currentUsersLatLong.getLatitude(), currentUsersLatLong.getLongitude(), location.getLatitude(), location.getLongitude());
        // LevenshteinDistance.Compute(baseString, stringtoTest)
        return 0.0;
    }
    
          /*
    Calculating the distances in kilometer
     */
    
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        //Log.e(TAG, "The distance is " + dist);
        return (dist);
    }
    
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    
    /**
     * 更新指示器
     */
    private void updateIndicatorTv() {
        int totalNum = viewPager.getAdapter().getCount();
        int currentItem = viewPager.getCurrentItem() + 1;
        indicatorTv.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + totalNum));
    }
    
    
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        FirebaseAuth.AuthStateListener stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    startActivity(new Intent(getActivity(), SignIn.class));
            }
        };
        //downloadList();
        //checkForNotification();
        Log.e(TAG, "On resume called in pagerTransition!");
    }
    
    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        if (ref != null && childEventListener != null)
            ref.removeEventListener(childEventListener);
        displayArrayList.clear();
    }
    
    @Override
    public void onDestroy() {
        cleanup();
        super.onDestroy();
    }
}
