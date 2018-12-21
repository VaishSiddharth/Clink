package com.testlabic.datenearu.TransitionUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.Share;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.testlabic.datenearu.Models.LatLong;
import com.testlabic.datenearu.Models.ModelPrefs;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.locationUpdater;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class pagerTransition extends Fragment {
    
    private static final String TAG = pagerTransition.class.getSimpleName();
    private TextView indicatorTv;
    private View positionView;
    private ViewPager viewPager;
    View rootView;
    String interestedGender;
    SharedPreferences preferences;
    private List<CommonFragment> fragments = new ArrayList<>(); // 供ViewPager使用
    private ArrayList<ModelUser> displayArrayList;
    private TextView changeLocation;
    private FragmentStatePagerAdapter adapter;
    private SharedPreferences sharedPreferences;
    private ImageButton filterList;
    private SeekBar age_seek;
    private SeekBar distance_seek;
    private LatLong currentUsersLatLong;
    private ModelPrefs prefs;
    
    public pagerTransition() {
        // Required empty public constructor
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        // 1. 沉浸式状态栏
        
        rootView = inflater.inflate(R.layout.activity_pager_transition, viewPager, false);
        if(Constants.uid!=null)
        fetchPreferences();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        interestedGender = sharedPreferences.getString(Constants.userIntrGender, null);
        sharedPreferences.edit();
        positionView = rootView.findViewById(R.id.position_view);
        filterList = rootView.findViewById(R.id.filter);
        changeLocation = rootView.findViewById(R.id.changeLocation);
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), locationUpdater.class));
            }
        });
        initImageLoader();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        downloadList();
        
        if (!sharedPreferences.getBoolean(Constants.shownShowCaser, false))
            setupShowCaser();
        
        filterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        
        return rootView;
    }
    
    private void fetchPreferences() {
        DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userPreferences)
                .child(Constants.uid);
        refPrefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    prefs = dataSnapshot.getValue(ModelPrefs.class);
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
        final AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
        deleteDialog.setView(filterDialogView);
        deleteDialog.show();
        
        RadioGroup genderGroup = filterDialogView.findViewById(R.id.genderRadio);
        RadioButton maleRButton = filterDialogView.findViewById(R.id.maleRadio);
        RadioButton femaleRButton = filterDialogView.findViewById(R.id.femaleRadio);
        final TextView distance = filterDialogView.findViewById(R.id.distance_value);
        
        if(prefs!=null)
        {
            if(prefs.getPreferedGender().equals(Constants.male)) {
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
                if(checkedId==R.id.maleRadio){
                    //update preferences
                    DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.userPreferences)
                            .child(Constants.uid);
    
                    HashMap<String, Object> updateMap= new HashMap<>();
                    updateMap.put(Constants.preferedGender, Constants.male);
                    Toast.makeText(getActivity(), "Getting new results", Toast.LENGTH_SHORT).show();
                    refPrefs.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                                downloadList();
                        }
                    });
                    
                }
                
                else
                {
                    //update preferences
                    DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.userPreferences)
                            .child(Constants.uid);
    
                    HashMap<String, Object> updateMap= new HashMap<>();
                    updateMap.put(Constants.preferedGender, Constants.female);
                    Toast.makeText(getActivity(), "Getting new results", Toast.LENGTH_SHORT).show();
                    refPrefs.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            downloadList();
                        }
                    });
                }
            }
        });
        age_seek = filterDialogView.findViewById(R.id.age_seekbar);
        distance_seek = filterDialogView.findViewById(R.id.distance_seekbar);
        
        age_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            
            }
        });
    
        double dist=  prefs.getDistanceLimit();
        distance.setText(String.valueOf((int) dist)+" km");
        distance_seek.setProgress((int) dist);
        
        distance_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    
                distance.setText(String.valueOf((int) (double) progress +10)+" km");
                // update the database
                HashMap<String, Object> updateMap = new HashMap<>();
                updateMap.put("distanceLimit", (double) progress +10 );
                DatabaseReference refPrefs = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.userPreferences)
                        .child(Constants.uid);
                
                refPrefs.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        
                        downloadList();
        
                    }
                });
            }
    
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
        
            }
    
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
        
            }
        });
        
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
                    CommonFragment fragment = fragments.get(position % 10);
                    fragment.bindData(displayArrayList.get(position % displayArrayList.size()));
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
        String city = preferences.getString(Constants.cityLabel, "Lucknow_Uttar Pradesh_India");
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels).child(city);
        
        ref.addChildEventListener(new ChildEventListener() {
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
                /*
                populate adapter...
                 */
                fillViewPager(displayArrayList);
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
    }
    
    private void filterList(ModelUser item) {
        //pass the item through the filters and then add them to the list for adaper;
        //1. Age filter
        if (prefs!=null&&item.getNumeralAge() >= prefs.getMinAge() && item.getNumeralAge() <= prefs.getMaxAge() && distanceBetweenThem(item.getLocation()) <= prefs.getDistanceLimit()) {
            displayArrayList.add(item);
        }
    }
    
    private double distanceBetweenThem(LatLong location) {
        
        if(currentUsersLatLong!=null)
        return distance(currentUsersLatLong.getLatitude(), currentUsersLatLong.getLongitude(), location.getLatitude(), location.getLongitude());
        
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
        Log.e(TAG, "The distance is " + dist);
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
    
    /**
     * 调整沉浸式菜单的title
     */
    private void dealStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = positionView.getLayoutParams();
            lp.height = statusBarHeight;
            positionView.setLayoutParams(lp);
        }
    }
    
    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
    
    @SuppressWarnings("deprecation")
    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity())
                .memoryCacheExtraOptions(480, 800)
                // default = device screen dimensions
                .threadPoolSize(3)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13) // default
                .discCacheSize(50 * 1024 * 1024) // 缓冲大小
                .discCacheFileCount(100) // 缓冲文件数目
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(getActivity())) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();
        
        // 2.单例ImageLoader类的初始化
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }
    
    private void putValueInchangeLocation() {
        
        if (Constants.uid != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo).child(Constants.uid).child("cityLabel");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    
                    if (dataSnapshot.getValue() != null) {
                        String value = dataSnapshot.getValue(String.class);
                        changeLocation.setText(value);
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        putValueInchangeLocation();
        //downloadList();
        //checkForNotification();
        // Log.e(TAG, "On resume called!");
    }
    
}
