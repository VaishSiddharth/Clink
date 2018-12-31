package com.testlabic.datenearu.BillingUtils;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PurchasePacks extends AppCompatActivity implements PurchasesUpdatedListener {
    private static final String TAG = PurchasePacks.class.getSimpleName();
    public BillingClient mBillingClient;
    TextView pack1;
    StartAppAd startAppAd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_packs);
        pack1 = findViewById(R.id.firstPack);
        TextView rewardAd = findViewById(R.id.rewardAd);
        startAppAd = new StartAppAd(PurchasePacks.this);
        rewardAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            }
        });
    
        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                // Grant user with the reward
    
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.xPoints)
                        .child(Constants.uid);
                HashMap<String, Object> updatePoints = new HashMap<>();
                updatePoints.put(Constants.xPoints, 10000);
                reference.updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        new SweetAlertDialog(PurchasePacks.this)
                                .setTitleText("Points updated!")
                                .setContentText("Purchase Successful")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    finish();
                            }
                        }, 1500);
                    }
                });
                
            }
        });
        // create new Person
        //testData();
        
    }
    
    private void testData() {
        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    queryPackages();
                    
                }
            }
            
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                mBillingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(int responseCode) {
                    
                    }
                    
                    @Override
                    public void onBillingServiceDisconnected() {
                    
                    }
                });
            }
        });
        
        Log.e(TAG, "Showing purchase data");
        
    }
    
    private void queryPackages() {
        List<String> skuList = new ArrayList<>();
        skuList.add("186_points");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        Log.e(TAG, "The responses are " + responseCode);
                        if (responseCode == BillingClient.BillingResponse.OK
                                && skuDetailsList != null) {
                            for (final SkuDetails skuDetails : skuDetailsList) {
                                final String desc = skuDetails.getDescription();
                                final String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                
                                pack1.setText(desc);
                                
                                pack1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        
                                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                .setSku("android.test.purchased")
                                                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                                                .build();
                                        int r = mBillingClient.launchBillingFlow(PurchasePacks.this, flowParams);
    
                                    }
                                    });
                                }
                            }
                        }
                        // Process the result.
                    }
        );
                }
        
        @Override
        public void onPurchasesUpdated ( int responseCode, @Nullable List<Purchase> purchases){
            if (responseCode == BillingClient.BillingResponse.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
        
        private void handlePurchase (Purchase purchase){
            String test = purchase.getOriginalJson();
            String sku = purchase.getSku();
            //  if(sku.equals(Constants.firstPack))
            {
                //update points
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.xPoints)
                        .child(Constants.uid);
                HashMap<String, Object> updatePoints = new HashMap<>();
                updatePoints.put(Constants.xPoints, 110);
                reference.updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        new SweetAlertDialog(PurchasePacks.this)
                                .setTitleText("Points updated!")
                                .setContentText("Purchase Successful")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                });
            }
            Log.e(TAG, test);
        }
        
    }