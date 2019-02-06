package com.testlabic.datenearu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import jp.wasabeef.blurry.Blurry;

public class EditActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_I = 48;
    private static final int SELECT_IMAGE_II = 58;
    private static final int SELECT_IMAGE_III = 68;
    private static final String TAG = EditActivity.class.getSimpleName();
    TextView name, age, about;
    ImageView image1, nameWrap,image2,image3;
    Switch blur;
    String cityLabel, gender;
    Boolean detailsSetup = false;
    int SELECT_IMAGE =1;
    FirebaseStorage storage ;
    // Create a storage reference from our app
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    // Create a reference to "mountains.jpg"
    final StorageReference displaypics_Ref = storageRef.child("Display_Pics");
    Task<Uri> downloadUrl;
    UploadTask uploadTask;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        about = findViewById(R.id.about);
        View dp1=findViewById(R.id.edit_dp_1);
        View dp2=findViewById(R.id.edit_dp_2);
        View dp3=findViewById(R.id.edit_dp_3);
        image1 = dp1.findViewById(R.id.image1);
        image2 = dp2.findViewById(R.id.image1);
        image3 = dp3.findViewById(R.id.image1);
        blur = findViewById(R.id.blurProfile);
        nameWrap = findViewById(R.id.name_wrap);




        //open gallery code on image click
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });




        blur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(!detailsSetup)
                        Toast.makeText(EditActivity.this, "Wait a moment and try again please!", Toast.LENGTH_SHORT).show();
                    else
                    blurProfile();
                }
                else {
                    if(!detailsSetup)
                        Toast.makeText(EditActivity.this, "Wait a moment and try again please!", Toast.LENGTH_SHORT).show();
                    else
                    unBlurProfile();
                }
            }
        });
        setUpDetails();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap=null;
                if (data != null) {
                    try {
                        Log.e(TAG, "Upload step1");
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        image1.setImageBitmap(bitmap);


                        //image1.setDrawingCacheEnabled(true);
                       //image1.buildDrawingCache();
                        //Bitmap bitmap1 = image1.getDrawingCache();


                        /*if (bitmap1 != null && !bitmap1.isRecycled()) {
                            bitmap1.recycle();
                            bitmap1 = null;
                        }*/


                        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data1 = baos.toByteArray();
                        UploadTask uploadTask = displaypics_Ref.putBytes(data1);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.e(TAG, "Failed!");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.e(TAG, "Upload step2");
                                downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();//contains file metadata such as size, content-type, and download URL.
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);

                                HashMap<String, Object> updateimage = new HashMap<>();
                                updateimage.put("image2", downloadUrl);
                                ref.updateChildren(updateimage);
                            }
                        });*/



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploaddp(bitmap);
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
        //TODO: Later
    }

    public void uploaddp(Bitmap bitmap){














        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                Log.e(TAG, "Upload step2");

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return displaypics_Ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo).child(Constants.uid);
                            //Toast.makeText(getApplicationContext(),downloadUri.toString(),Toast.LENGTH_LONG).show();
                            HashMap<String, Object> updateimage = new HashMap<>();
                            updateimage.put("image2", String.valueOf(downloadUri));
                            ref.updateChildren(updateimage);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

















                /*downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();//contains file metadata such as size, content-type, and download URL.
                String test = displaypics_Ref.getDownloadUrl().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
                Toast.makeText(getApplicationContext(),test,Toast.LENGTH_LONG).show();
                HashMap<String, Object> updateimage = new HashMap<>();
                updateimage.put("image2", displaypics_Ref.getDownloadUrl());*/
                //ref.updateChildren(updateimage);
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

                        startActivity(new Intent(EditActivity.this, EditActivity.class));
                        finish();
                    }
                });
            
            }
        });
      
    }
    
    private void blurProfile() {
        Blurry.with(EditActivity.this).capture(image1).into(image1);
//        Blurry.with(EditActivity.this).capture(nameWrap).into(nameWrap);
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
        
        if (Build.VERSION.SDK_INT >= 11) {
            name.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        float radius = name.getTextSize() / 3;
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        name.getPaint().setMaskFilter(filter);
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
                        Glide.with(EditActivity.this).load(user.getImageUrl()).into(image2);
                        cityLabel = user.getCityLabel();
                        cityLabel = cityLabel.replace(", ", "_");
                        gender = user.getGender();
                        detailsSetup = true;
                        if(user.getIsBlur())
                        {
                           blur.setChecked(true);
                           blurProfile();
                        }
                        else blur.setChecked(false);
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
    
}
