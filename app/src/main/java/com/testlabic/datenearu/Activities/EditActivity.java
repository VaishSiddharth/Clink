package com.testlabic.datenearu.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.soundcloud.android.crop.Crop;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditActivity extends AppCompatActivity {
    
    String imageId = "image1";
    
    private static final String default_uri_dp = "https://firebasestorage.googleapis.com/v0/b/datenearu.appspot.com/o/final_app_logo1.png?alt=media&token=e9463a91-4f57-412d-a4be-c26d2e06d84e";
    private static final String TAG = EditActivity.class.getSimpleName();
    TextView name, age, about,blurinfo;
    ImageView image1, nameWrap, image2, image3,previewprofile,backbutton;
    ProgressBar bar1, bar2, bar3;
    Switch blur;
    ImageView remove_dp1, remove_dp2, remove_dp3;
    String cityLabel, gender;
    Boolean detailsSetup = false;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference displaypics_Ref;
    UploadTask uploadTask;
    View toolbar;
    boolean blurTrialEnded = false;
    boolean switchedManually = false;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        about = findViewById(R.id.about);
        View dp1 = findViewById(R.id.edit_dp_1);
        View dp2 = findViewById(R.id.edit_dp_2);
        View dp3 = findViewById(R.id.edit_dp_3);
        image1 = dp1.findViewById(R.id.image1);
        image2 = dp2.findViewById(R.id.image1);
        image3 = dp3.findViewById(R.id.image1);
        bar1 = dp1.findViewById(R.id.progress_bar);
        bar2 = dp2.findViewById(R.id.progress_bar);
        bar3 = dp3.findViewById(R.id.progress_bar);
        blur = findViewById(R.id.blurProfile);
        nameWrap = findViewById(R.id.name_wrap);
        remove_dp1 = dp1.findViewById(R.id.remove_dp);
        remove_dp2 = dp2.findViewById(R.id.remove_dp);
        remove_dp3 = dp3.findViewById(R.id.remove_dp);
        toolbar=findViewById(R.id.toolbar);
        backbutton=toolbar.findViewById(R.id.backbutton1);
        previewprofile=toolbar.findViewById(R.id.previewprofile);
        blurinfo=findViewById(R.id.blurinfo);


        image1.setImageResource(R.drawable.final_app_logo1bw);
        image2.setImageResource(R.drawable.final_app_logo1bw);
        image3.setImageResource(R.drawable.final_app_logo1bw);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.super.onBackPressed();
            }
        });
        previewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditActivity.this,ProfilePreview.class));
            }
        });
        
        //remove dp code
        remove_dp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Are you sure?");
                sweetAlertDialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        bar1.setVisibility(View.VISIBLE);
                        sweetAlertDialog.dismiss();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                image1.setImageResource(R.drawable.final_app_logo1bw);
                                uri_update_db(default_uri_dp, "image1");
                                bar1.setVisibility(View.GONE);
                            }
                        },1000);
                    }
                });
                sweetAlertDialog.setCancelText("No");
                sweetAlertDialog.show();
            }
        });
        remove_dp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Are you sure?");
                sweetAlertDialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        bar2.setVisibility(View.VISIBLE);
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bar2.setVisibility(View.GONE);
                                image2.setImageResource(R.drawable.final_app_logo1bw);
                                uri_update_db(default_uri_dp, "image2");

                            }
                        },1000);
                    }
                });
                sweetAlertDialog.setCancelText("No");
                sweetAlertDialog.show();
            }
        });
        remove_dp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Are you sure?");
                sweetAlertDialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        bar3.setVisibility(View.VISIBLE);
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bar3.setVisibility(View.GONE);
                                image3.setImageResource(R.drawable.final_app_logo1bw);
                                uri_update_db(default_uri_dp, "image3");


                            }
                        },1000);
                    }
                });
                sweetAlertDialog.setCancelText("No");
                sweetAlertDialog.show();
            }
        });
        
        //open gallery  or camera code on image click
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.NORMAL_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Change picture by clicking on any one of them\n");
                sweetAlertDialog.setConfirmButton("Gallery", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        
                        Crop.pickImage(EditActivity.this);
                        sweetAlertDialog.dismiss();
                        
                        imageId = "imageUrl";
                    }
                });
                
                sweetAlertDialog.show();
                
                Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(EditActivity.this, R.drawable.button_4_dialogue));
                
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.NORMAL_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Change picture by clicking on any one of them\n");
                sweetAlertDialog.setConfirmButton("Gallery", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Crop.pickImage(EditActivity.this);
                        
                        sweetAlertDialog.dismiss();
                        imageId = "image2";
                    }
                });
                sweetAlertDialog.show();
                Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(EditActivity.this, R.drawable.button_4_dialogue));
    
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.NORMAL_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Change picture by clicking on any one of them\n");
                sweetAlertDialog.setConfirmButton("Gallery", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Crop.pickImage(EditActivity.this);
                        sweetAlertDialog.dismiss();
                        imageId = "image3";
                    }
                });
                
                sweetAlertDialog.show();
                Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(EditActivity.this, R.drawable.button_4_dialogue));
    
    
            }
        });
        
        blur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!detailsSetup)
                        Toast.makeText(EditActivity.this, "Wait a moment and try again please!", Toast.LENGTH_SHORT).show();
                    else {
                        if(switchedManually&&!blurTrialEnded)
                            blurProfile();
                        else if(blurTrialEnded)
                        {
                            blur.setChecked(false);
                            Toast.makeText(EditActivity.this, "Trial Expired!", Toast.LENGTH_SHORT).show();
                            final SweetAlertDialog alertDialog = new SweetAlertDialog(EditActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Trial period Over!")
                                    .setContentText("If you want to extend the blur duration by 7 days, spend 500 drops\n")
                                    .setConfirmButton("500 drops", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            deductDropsAndIncreaseBlurTime( sweetAlertDialog);
                    
                                        }
                                    });
                            alertDialog.show();
                            Button btn = alertDialog.findViewById(R.id.confirm_button);
                            btn.setBackground(ContextCompat.getDrawable(EditActivity.this, R.drawable.button_4_dialogue));
                            Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                            btn1.setBackground(ContextCompat.getDrawable(EditActivity.this, R.drawable.button_4_dialogue));
    
                            {
                                btn.setTypeface(Utils.SFPRoLight(EditActivity.this));
                                btn1.setTypeface(Utils.SFPRoLight(EditActivity.this));
        
                                TextView title = alertDialog.findViewById(R.id.title_text);
                                if(title!=null)
                                    title.setTypeface(Utils.SFProRegular(EditActivity.this));
        
                                TextView contentText = alertDialog.findViewById(R.id.content_text);
                                if(contentText!=null)
                                    contentText.setTypeface(Utils.SFPRoLight(EditActivity.this));
                            }
    
                            
                        }
                    }
                } else {
                    if (!detailsSetup)
                        Toast.makeText(EditActivity.this, "Wait a moment and try again please!\n", Toast.LENGTH_SHORT).show();
                    else if(switchedManually)
                        unBlurProfile();
                }
            }
        });
        //setUpDetails();
        blurTrialCalc();
    }
    
    private void deductDropsAndIncreaseBlurTime(final SweetAlertDialog sDialog) {
        
        DatabaseReference attemptRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        
        ValueEventListener attemptListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                if (modelSubscr != null) {
                    int current = modelSubscr.getXPoints();
                    if (current < Constants.unBlurForSevenDaysDrops) {
                        Toast.makeText(EditActivity.this, "You don't have enough points, buy now!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditActivity.this, PurchasePacks.class));
                        
                    } else {
                        current -= Constants.unBlurForSevenDaysDrops;
                        HashMap<String, Object> updatePoints = new HashMap<>();
                        updatePoints.put(Constants.xPoints, current);
                        Log.v(TAG, "Updating the drops here");
                        dataSnapshot.getRef().updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sDialog
                                        .setTitleText("Increasing duration!")
                                        .setContentText("Your profile will remain unblurred for 7 days from today!\n")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.userInfo).child(Constants.uid);
                               
                               //blur profile
    
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.userInfo).child(Constants.uid);
    
                                // first update userinfo then update child under city label node.
    
                                final HashMap<String, Object> updateBlur = new HashMap<>();
                                updateBlur.put(Constants.isBlur, true);
                                reference.updateChildren(updateBlur).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
            
                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.cityLabels).child(cityLabel).child(gender).child(Constants.uid);
                                        ref2.updateChildren(updateBlur);
                                    }
                                });
                                
                                HashMap<String, Object> update_blur_trial_ended = new HashMap<>();
                                update_blur_trial_ended.put("blurTrialEnded", false);
                                ref1.updateChildren(update_blur_trial_ended);
                                
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //update the blurStartTime
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.userInfo).child(Constants.uid).child("blurStartTime");
                                        HashMap<String, Object> updateMap = new HashMap<>();
                                        updateMap.put(Constants.timeStamp, ServerValue.TIMESTAMP);
                                        ref.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sDialog.dismiss();
                                            }
                                        });
                                        
                                    }
                                }, 2500);
                            }
                        });
                        
                    }
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        attemptRef.addListenerForSingleValueEvent(attemptListener);
        
    }
    
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        
    }
    public void blurTrialCalc()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(Constants.uid).child("blurStartTime").child(Constants.timeStamp);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    long timestamp = dataSnapshot.getValue(Long.class);
                    long epoch = System.currentTimeMillis();
                    long oneday = 86400000;
                    long trialend=timestamp+7*oneday;
                    long daysleft=(trialend-epoch)/oneday;
                    if(daysleft<=0)
                        blur.setChecked(false);
                    else {
                        if(!blurTrialEnded)
                        blurinfo.setText("Blur profile ("+daysleft+" days remaining)");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }
    
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
                switch (imageId) {
                    case "image2":
                        displaypics_Ref = storageRef.child("Display_Pics/" + Constants.uid + "/2.jpg");
                        image2.setImageURI(Crop.getOutput(result));
                        bar2.setVisibility(View.VISIBLE);
                        bar1.setVisibility(View.INVISIBLE);
                        bar3.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Please wait image is being uploaded",Toast.LENGTH_LONG).show();
                        
                        break;
                    case "image3":
                        displaypics_Ref = storageRef.child("Display_Pics/" + Constants.uid + "/3.jpg");
                        image3.setImageURI(Crop.getOutput(result));
                        bar3.setVisibility(View.VISIBLE);
                        bar1.setVisibility(View.INVISIBLE);
                        bar2.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Please wait image is being uploaded",Toast.LENGTH_LONG).show();
                        
                        break;
                    default:
                        displaypics_Ref = storageRef.child("Display_Pics/" + Constants.uid + "/1.jpg");
                        image1.setImageURI(Crop.getOutput(result));
                        bar1.setVisibility(View.VISIBLE);
                        bar2.setVisibility(View.INVISIBLE);
                        bar3.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Please wait image is being uploaded",Toast.LENGTH_LONG).show();
                        
                        break;
                }
                uploaddp(bitmap, imageId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    public void uploaddp(Bitmap bitmap, final String image_id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] data1 = baos.toByteArray();
        uploadTask = displaypics_Ref.putBytes(data1);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "Failed!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        
                        // Continue with the task to get the download URL
                        return displaypics_Ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String uri = downloadUri.toString();
                            uri_update_db(uri, image_id);
                            
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to get Uri", Toast.LENGTH_LONG).show();
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        });
        
    }
    
    public void uri_update_db(String uri, String image_id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        HashMap<String, Object> updateimage = new HashMap<>();
        updateimage.put(image_id, uri);
        //update imageurl for the current user;
        
        if(image_id.equals("imageUrl")&&gender!=null&&cityLabel!=null) {
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.cityLabels)
                    .child(Constants.encrypt(cityLabel))
                    .child(gender).child(Constants.uid);
            HashMap<String, Object> update_image_url_city = new HashMap<>();
            update_image_url_city.put("imageUrl", uri);
            ref2.updateChildren(update_image_url_city);
        }
        ref.updateChildren(updateimage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                bar1.setVisibility(View.INVISIBLE);
                bar2.setVisibility(View.INVISIBLE);
                bar3.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Successful!!",Toast.LENGTH_LONG).show();
                
            }
        });
    }
    
    private void unBlurProfile() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(Constants.uid);
        
        // first update userinfo then update child under city label node.
        
        final HashMap<String, Object> updateBlur = new HashMap<>();
        updateBlur.put(Constants.isBlur, false);
        reference.updateChildren(updateBlur).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.cityLabels).child(cityLabel).child(gender).child(Constants.uid);
                ref2.updateChildren(updateBlur).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        
                        startActivity(new Intent(EditActivity.this, ProfilePreview.class));
                        //finish();
                    }
                });
                
            }
        });
        
    }
    
    private void blurProfile() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(Constants.uid);
        
        // first update userinfo then update child under city label node.
        
        final HashMap<String, Object> updateBlur = new HashMap<>();
        updateBlur.put(Constants.isBlur, true);
        reference.updateChildren(updateBlur).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.cityLabels).child(cityLabel).child(gender).child(Constants.uid);
                ref2.updateChildren(updateBlur).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(EditActivity.this, ProfilePreview.class));
                    }
                });
                
            }
        });
        
        /*name.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        float radius = name.getTextSize() / 3;
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        name.getPaint().setMaskFilter(filter);*/
    }
    
    private void setUpDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(ModelUser.class) != null) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (user != null && user.getUserName() != null) {
                        name.setText(user.getUserName());
                        age.setText(String.valueOf(user.getNumeralAge()));
                        Glide.with(EditActivity.this).load(user.getImageUrl()).into(image1);
                        Glide.with(EditActivity.this).load(user.getImage2()).into(image2);
                        Glide.with(EditActivity.this).load(user.getImage3()).into(image3);
                        cityLabel = user.getCityLabel();
                        cityLabel = cityLabel.replace(", ", "_");
                        gender = user.getGender();
                        detailsSetup = true;
    
                        blurTrialEnded = user.isBlurTrialEnded();
                        
                        if (user.getIsBlur()) {
                            blur.setChecked(true);
                            switchedManually = true;
                            /*RequestOptions myOptions = new RequestOptions()
                                    .override(15, 15)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE);

                            Glide.with(EditActivity.this).load(user.getImageUrl()).apply(myOptions).into(image1);
                            Glide.with(EditActivity.this).load(user.getImage2()).apply(myOptions).into(image2);
                            Glide.with(EditActivity.this).load(user.getImage3()).apply(myOptions).into(image3);
                            blurProfile();*/
                        } else {
                            blur.setChecked(false);
                            switchedManually = true;
                        }
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().getRef().
                                child(Constants.cityLabels).child(Constants.encrypt(user.getCityLabel())).child(user.getGender()).child(user.getUid());
                        HashMap<String, Object> update_image_url_city = new HashMap<>();
                        update_image_url_city.put("imageUrl", user.getImageUrl());
                        ref2.updateChildren(update_image_url_city);
                    }
                    
                    if (user != null && user.getAbout() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            about.setText(Html.fromHtml(user.getAbout(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            about.setText(Html.fromHtml(user.getAbout()));
                        }
                    }
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpDetails();
    }
}
