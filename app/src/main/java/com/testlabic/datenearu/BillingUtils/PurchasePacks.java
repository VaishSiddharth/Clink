package com.testlabic.datenearu.BillingUtils;

import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.testlabic.datenearu.AttemptMatchUtils.QuestionsAttemptActivity;
import com.testlabic.datenearu.Models.ModelPurchase;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PurchasePacks extends AppCompatActivity implements PurchasesUpdatedListener {
    private static final String TAG = PurchasePacks.class.getSimpleName();
    public BillingClient mBillingClient;
    TextView pack1, pack2, pack3,pack4;
    ImageView finish;
    StartAppAd startAppAd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_packs);
        pack1 = findViewById(R.id.firstPack);
        pack2 = findViewById(R.id.secondPack);
        pack3 = findViewById(R.id.thirdPack);
        pack4 = findViewById(R.id.fourthPack);
        finish=findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        TextView rewardAd = findViewById(R.id.rewardAd);
        startAppAd = new StartAppAd(PurchasePacks.this);
        rewardAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Log.e(TAG, "Click receied to play ad!");
                // startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
                startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                    @Override
                    public void onReceiveAd(Ad ad) {
                        Log.i("startApp", "rewarded Loaded");
                        startAppAd.showAd();
                    }
                    
                    @Override
                    public void onFailedToReceiveAd(Ad ad) {
                        
                        Toast.makeText(getApplicationContext(), ad.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                startAppAd.setVideoListener(new VideoListener() {
                    @Override
                    public void onVideoCompleted() {
                        Log.i("VideoWatched", "watched");
                        startAppAd.close();
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.xPoints)
                                .child(Constants.uid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                
                                ModelSubscr subscr = dataSnapshot.getValue(ModelSubscr.class);
                                if (subscr != null) {
                                    int current = subscr.getXPoints();
                                    
                                    current += Constants.rewardAdPoints;
                                    HashMap<String, Object> updatePoints = new HashMap<>();
                                    updatePoints.put(Constants.xPoints, current);
                                    
                                    reference.updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            
                                            Toast.makeText(PurchasePacks.this, "You received 10 drops, watch again to get more!", Toast.LENGTH_SHORT).show();
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
                            }
                            
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            
                            }
                        });
                        
                    }
                });
            }
        });
        
        setUpBilling();
        pack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    
    private void setUpBilling() {
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
        skuList.add("100_drops");
        skuList.add("200_drops");
        skuList.add("500_drops");
        skuList.add("1000_drops");
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
                                String desc = skuDetails.getDescription();
                                final String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                if (sku != null && sku.equals("100_drops")) {
                                    desc = desc + " for " + price;
                                    pack1.setText(desc);
                                    pack1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                    .setSku(sku)
                                                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                                                    .build();
                                            int responseCode = mBillingClient.launchBillingFlow(PurchasePacks.this, flowParams);
                                            //Toast.makeText(PurchasePacks.this, "The response code is " + responseCode, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else if (sku != null && sku.equals("200_drops")) {
                                    desc = desc + " for " + price;
                                    pack2.setText(desc);
                                    pack2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                    .setSku(sku)
                                                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                                                    .build();
                                            int responseCode = mBillingClient.launchBillingFlow(PurchasePacks.this, flowParams);
                                            //Toast.makeText(PurchasePacks.this, "The response code is " + responseCode, Toast.LENGTH_SHORT).show();
                                            
                                            //Purchase purchase = mBillingClient.queryPurchases(sku).getPurchasesList().get(0);
                                            
                                            final ConsumeResponseListener listener = new ConsumeResponseListener() {
                                                @Override
                                                public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String outToken) {
                                                    if (responseCode == BillingClient.BillingResponse.OK) {
                                                        // Handle the success of the consume operation.
                                                        // For example, increase the number of coins inside the user's basket.
                                                       // Toast.makeText(PurchasePacks.this, "Consumption successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            };
                                            // mBillingClient.consumeAsync(purchase.getPurchaseToken(), listener);
                                            
                                        }
                                    });
                                } else if (sku != null && sku.equals("500_drops")) {
                                    
                                    desc = desc + " for " + price;
                                    pack3.setText(desc);
                                    pack3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                    .setSku(sku)
                                                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                                                    .build();
                                            int responseCode = mBillingClient.launchBillingFlow(PurchasePacks.this, flowParams);
                                            //Toast.makeText(PurchasePacks.this, "The response code is " + responseCode, Toast.LENGTH_SHORT).show();
                                            
                                        }
                                    });
                                }
                                else if (sku != null && sku.equals("1000_drops")) {
    
                                    desc = desc + " for " + price;
                                    pack4.setText(desc);
                                    pack4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                    .setSku(sku)
                                                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                                                    .build();
                                            int responseCode = mBillingClient.launchBillingFlow(PurchasePacks.this, flowParams);
                                            Toast.makeText(PurchasePacks.this, "The response code is " + responseCode, Toast.LENGTH_SHORT).show();
            
                                        }
                                    });
                                }
                            }
                        }
                    }
                    // Process the result.
                }
        );
    }
    
    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (final Purchase purchase : purchases) {
                //consume Async
                ConsumeResponseListener listener = new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String outToken) {
                        if (responseCode == BillingClient.BillingResponse.OK) {
                            // Handle the success of the consume operation.
                            // For example, increase the number of coins inside the user's basket.
                            handlePurchase(purchase);
                            Toast.makeText(PurchasePacks.this, "Consumed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), listener);
                
                //handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }
    
    private void handlePurchase(Purchase purchase) {
        //push Purchase to database;
        
        
        String test = purchase.getOriginalJson();
        String sku = purchase.getSku();
        int updatePoint;
        
        switch (sku) {
            case Constants.SKU_100_drops:
                updatePoint = 100;
                break;
            
            case Constants.SKU_200_drops:
                updatePoint = 200;
                break;
            
            case Constants.SKU_500_drops:
                updatePoint = 500;
                break;
            
            case Constants.SKU_1000_drops:
                updatePoint = 1000;
                break;
            
            default:
                updatePoint = 99999;
                break;
        }
        initiatedPurchaseData(updatePoint, purchase);
    
        {
            //update points
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.xPoints)
                    .child(Constants.uid);
            final int finalUpdatePoint = updatePoint;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                    if (modelSubscr != null) {
                        int current = modelSubscr.getXPoints();
                        {
                            current += finalUpdatePoint;
                            HashMap<String, Object> updatePoints = new HashMap<>();
                            updatePoints.put(Constants.xPoints, current);
                            dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    final SweetAlertDialog alertDialog = new SweetAlertDialog(PurchasePacks.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Points updated!")
                                            .setContentText("Purchase Successful");
    
                                    alertDialog.show();
                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    }, 2000);
                                }
                            });
                            
                        }
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
        }
        Log.e(TAG, test);
    }
    
    private void initiatedPurchaseData(int updatePoint, Purchase purchase) {
        ModelPurchase modelPurchase = new ModelPurchase(purchase.getSku(), purchase.getOriginalJson(), purchase.getOrderId(), purchase.getPackageName(),
                purchase.getPurchaseToken(), purchase.getSignature(), updatePoint);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.purchaseLogs)
                .child(Constants.uid)
                .push();
        
        reference.setValue(modelPurchase);
        
    }
    
}