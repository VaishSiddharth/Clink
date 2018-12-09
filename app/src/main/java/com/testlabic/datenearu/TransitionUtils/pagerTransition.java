package com.testlabic.datenearu.TransitionUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.locationUpdater;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
    SharedPreferences preferences;
    
    private List<CommonFragment> fragments = new ArrayList<>(); // 供ViewPager使用
    private ArrayList<ModelUser> displayArrayList;
    private TextView changeLocation;
    private FragmentStatePagerAdapter adapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    public pagerTransition() {
        // Required empty public constructor
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        // 1. 沉浸式状态栏
        
        rootView = inflater.inflate(R.layout.activity_pager_transition, viewPager, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        positionView = rootView.findViewById(R.id.position_view);
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

        if(!sharedPreferences.getBoolean(Constants.shownShowCaser, false))
        setupShowCaser();
        return rootView;
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
    }
    
    /**
     * 填充ViewPager
     *
     * @param displayArrayList
     */
    private void fillViewPager(final ArrayList<ModelUser> displayArrayList) {
        
        indicatorTv = (TextView) rootView.findViewById(R.id.indicator_tv);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        
        viewPager.setPageTransformer(false, new CustPagerTransformer(getActivity()));
        
        for (int i = 0; i < displayArrayList.size(); i++) {
            fragments.add(new CommonFragment());
        }
        
        if(getActivity()!=null) {
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
        String city = preferences.getString(Constants.cityLabel, "Lucknow_Uttar Pradesh_India");
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels).child(city);
        
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                if (dataSnapshot.getValue() != null) {
                    ModelUser item = dataSnapshot.getValue(ModelUser.class);
                    if (item != null)
                        displayArrayList.add(item);
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
