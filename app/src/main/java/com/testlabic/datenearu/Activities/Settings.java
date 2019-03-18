package com.testlabic.datenearu.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

public class Settings extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = Settings.class.getSimpleName();
    public GoogleApiClient mGoogleApiClient;
    LinearLayout signOut, deleteAccount, shareApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        signOut = findViewById(R.id.sign_out);
        deleteAccount = findViewById(R.id.delete_account);
        shareApp = findViewById(R.id.share_app);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
    
        mGoogleApiClient = new GoogleApiClient.Builder(Settings.this)
                .enableAutoManage(Settings.this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Error occured "+connectionResult.getErrorMessage());
                    }
                })
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
            
                .build();
    
      
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_Account();
            }
        });
    }
    
    private void delete_Account() {
        SweetAlertDialog alertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirm Deletion")
                .setTitleText("Are you sure you want to leave us?")
                .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteuserInfo(sweetAlertDialog);
                    }
                });
        alertDialog.show();
    }
    
    
    private void deleteuserInfo(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Account Deleted!");
        sweetAlertDialog.setContentText("You'll be hidden from the users until you sign in again!");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.cityLabels)
                .child(Constants.all_location)
                .child(Constants.uid)
                ;
        
        reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                    logout();
            }
        });
        //TODO: check all refrences and delete data!
        
    }
    
    public void logout() {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                
                FirebaseAuth.getInstance().signOut();
                if(LoginManager.getInstance()!=null)
                {
                    LoginManager.getInstance().logOut();
                }
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                    startActivity(new Intent(Settings.this, SignIn.class));
                            }
                        }
                    });
                }
            }
            
            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
        
        
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    
    }
}
