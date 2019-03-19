package com.testlabic.datenearu.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.testlabic.datenearu.Activities.EditActivity;
import com.testlabic.datenearu.Activities.MainActivity;
import com.testlabic.datenearu.Activities.ProfilePreview;
import com.testlabic.datenearu.BillingUtils.PurchasePacks;
import com.testlabic.datenearu.Models.ModelSubscr;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.ProfileUtils.UploadPhotos;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.testlabic.datenearu.Adapters.ProfileAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.ProfileModel;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    
    public ProfileFragment() {
        // Required empty public constructor
    }
    private static final String default_uri_dp = "https://firebasestorage.googleapis.com/v0/b/datenearu.appspot.com/o/final_app_logo1.png?alt=media&token=e9463a91-4f57-412d-a4be-c26d2e06d84e";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerview;
    private ArrayList<ProfileModel> profileModelArrayList;
    private View rootView;
    private TextView username, desc;
    private ImageView displayImage,remove_dp,edit,profilepreview;
    private TextView points, buy;
    View edit_dp;
    String gender,cityLabel;
    ProgressBar bar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    UploadTask uploadTask;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference displaypics_Ref;
    
    Integer inbox[] = {R.drawable.ic_inbox, R.drawable.ic_edit, R.drawable.ic_profile, R.drawable.ic_settings};
    Integer arrow[] = {R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp};
    String txttrades[] = {"My Questions", "Edit Profile", "About you", "Settings"};
    String txthistory[] = {"Your questions for matches", "Improve your profile", "Tap to Edit", "Delete account/ Sign out"};
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        username = rootView.findViewById(R.id.username);
        edit_dp=rootView.findViewById(R.id.edit_dp);
        desc = rootView.findViewById(R.id.about);
        edit = rootView.findViewById(R.id.edit);
        displayImage = edit_dp.findViewById(R.id.image1);
        remove_dp=edit_dp.findViewById(R.id.remove_dp);
        bar=edit_dp.findViewById(R.id.progress_bar);
        points = rootView.findViewById(R.id.points);
        buy = rootView.findViewById(R.id.buy);
        profilepreview=rootView.findViewById(R.id.profilepreview);
        profilepreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfilePreview.class));
            }
        });

        remove_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Are you sure?");
                sweetAlertDialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        bar.setVisibility(View.VISIBLE);
                        sweetAlertDialog.dismiss();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                displayImage.setImageResource(R.drawable.final_app_logo1bw);
                                uri_update_db(default_uri_dp, "imageUrl");
                                bar.setVisibility(View.GONE);
                            }
                        },1000);
                    }
                });
                sweetAlertDialog.setCancelText("No");
                sweetAlertDialog.show();
                Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_4_dialogue));
                Button btn1 = sweetAlertDialog.findViewById(R.id.cancel_button);
                btn1.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_4_dialogue));
            }
        });

        displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.NORMAL_TYPE);
                sweetAlertDialog.setTitle("Image");
                sweetAlertDialog.setContentText("Change your Display Picture");
                sweetAlertDialog.setConfirmButton("Gallery", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        //Crop.pickImage(EditActivity.this);
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getContext(),ProfileFragment.this);
                        //Log.e(TAG,getActivity().toString());
                        bar.setVisibility(View.VISIBLE);
                        sweetAlertDialog.dismiss();

                        //imageId = "imageUrl";
                    }
                });

                sweetAlertDialog.show();

                Button btn = sweetAlertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.button_4_dialogue));

            }
        });
        
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PurchasePacks.class));
            }
        });
        
        setUpPoints();
        fillDesc();
        
        /*
        fill profile details
         */
        
        fillProfile();
        
        recyclerview = rootView.findViewById(R.id.recycler1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        
        profileModelArrayList = new ArrayList<>();
        
        for (int i = 0; i < inbox.length; i++) {
            ProfileModel view = new ProfileModel(inbox[i], arrow[i], txttrades[i], txthistory[i]);
            profileModelArrayList.add(view);
        }
        profileAdapter = new ProfileAdapter(getActivity(), profileModelArrayList);
        recyclerview.setAdapter(profileAdapter);
        
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                displayImage.setImageURI(resultUri);
                Log.e(TAG,resultUri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), resultUri);
                    displaypics_Ref = storageRef.child("Display_Pics/" + Constants.uid + "/display_image.jpg");
                    uploaddp(bitmap, "imageUrl");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG,error.toString());
            }
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
                //Log.e(TAG, "Failed!");
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

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(uri))
                    .build();

            if (user != null) {
                user.updateProfile(profileUpdates);
            }
        }
        ref.updateChildren(updateimage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Successful!",Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void showEditDialog(String text) {
        
        final LayoutInflater factory = LayoutInflater.from(getActivity());
        final View editTextDialog = factory.inflate(R.layout.dialog_edittext, null);
        final EditText editText = editTextDialog.findViewById(R.id.ediText);
        editText.setText(text);
        
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                // .setTitleText("Send a one-time message (25 pts)?")
                .setCustomView(editTextDialog)
                .setConfirmButton("Update!", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        
                        //display edit text
                        String text = editText.getText().toString();
                        if (text.matches("")) {
                            Toast.makeText(getActivity(), "Can't be empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                                    .child(Constants.uid);
                            
                            final HashMap<String, Object> updateMap = new HashMap<>();
                            updateMap.put("oneLine", text);
                            
                            reference.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    
                                    //updateAgain
                                    sharedPreferences = getActivity().getSharedPreferences(Constants.userDetailsOff, Context.MODE_PRIVATE);
                                    final String cityLabel = sharedPreferences.getString(Constants.cityLabel, null);
                                    final String gender = sharedPreferences.getString(Constants.userGender, null);
                                    if (cityLabel != null) {
                                        if (gender == null) {
                                            DatabaseReference genderRef = FirebaseDatabase.getInstance().getReference()
                                                    .child(Constants.userInfo)
                                                    .child(Constants.uid).child("gender");
                                            genderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (dataSnapshot.getValue(String.class) != null) {
                                                            editor = sharedPreferences.edit();
                                                            editor.putString(Constants.userGender, dataSnapshot.getValue(String.class)).apply();
                                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                                                    .child(Constants.cityLabels).child(cityLabel).child(dataSnapshot.getValue(String.class)).child(Constants.uid);
                                                           // Log.e("Test", String.valueOf(reference1));
                                                            reference1.updateChildren(updateMap);
                                                        }
                                                    }
                                                }
                                                
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                
                                                }
                                            });
                                        } else {
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                                    .child(Constants.cityLabels).child(cityLabel).child(gender).child(Constants.uid);
                                            //Log.e("Test2", String.valueOf(reference1));
    
                                            reference1.updateChildren(updateMap);
                                        }
                                    }
                                    
                                    sDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
                                    sDialog
                                            .setTitleText("Successful!")
                                            .setContentText("Info updated!")
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sDialog.dismissWithAnimation();
                                        }
                                    }, 2000);
                                    
                                }
                            });
                            //TODO: update other places also;
                            
                        }
                        
                    }
                })
                .show();
        ;
    }
    
    private void fillDesc() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.userInfo)
                .child(Constants.uid)
                .child(Constants.oneLine);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                final String text = dataSnapshot.getValue(String.class);
                desc.setText(text);
                
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(text);
                        
                    }
                });
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void setUpPoints() {
        DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.xPoints)
                .child(Constants.uid);
        xPointsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                ModelSubscr modelSubscr = dataSnapshot.getValue(ModelSubscr.class);
                if (modelSubscr != null) {
                    int current = modelSubscr.getXPoints();
                    String set = String.valueOf(current);
                    points.setText(set);
                    
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
    }
    
    private void fillProfile() {
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null)
                username.setText(user.getDisplayName());
            
            if (user.getPhotoUrl() != null) {
                //Glide.with(getContext()).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(displayImage);
            }
        }
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRef().child(Constants.userInfo).child(Constants.uid);
        
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(ModelUser.class) != null) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    gender=user.getGender();
                    cityLabel=user.getCityLabel();
                    if (user != null && user.getUserName() != null && getContext() != null) {
                        Glide.with(getContext()).load(user.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(displayImage);
                        //Glide.with(Profile.this).load(user.getImageUrl()).into(displayImage);
                    }
                    
                }
                
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            
            }
        });
        
    }
    
}
