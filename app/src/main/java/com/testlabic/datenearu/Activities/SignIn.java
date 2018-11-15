package com.testlabic.datenearu.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.Arrays;

public class SignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 48;
    private static final String TAG = SignIn.class.getSimpleName();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    LinearLayout googleSignIn;
    LinearLayout facebookSignIn;
    ProgressDialog progressDialog;
    CallbackManager callbackManager;
    LoginButton loginButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        /* code for signIN */

        /*
        Initialize views
         */
        
        /*TextView privacyPolicy = findViewById(R.id.privacy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://sites.google.com/site/privacypolicyforfamily360/home";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });*/
        
        googleSignIn = findViewById(R.id.gmail);
        facebookSignIn = findViewById(R.id.facebook);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        
        /*google signIN*/
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        facebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*
                    Sign in Via Facebook
                     */
            }
        });


            /*
            Facebook
             */
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "photos");
        // If using in a fragment
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                if (loginResult != null)
                    handleFacebookAccessToken(loginResult.getAccessToken());
            }
            
            @Override
            public void onCancel() {
                // App code
            }
            
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        facebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("public_profile"));
            }
        });
    }
    
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            
                            /*
                            Call manual fix to update the photo of user
                             */
                            
                            ManualFix();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Boolean isnewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.e(TAG, "The user is a new user or not " + isnewUser);
                            FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                            if (isnewUser) {
                                /*
                                    move to setup the account/profile
                                     */
                                // for now update users database
                                
                                updateDatabaseWithUser(mCurrentUser);
                                
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                                finish();
                                
                            } else {
                                    /*
                                    move to main activity
                                     */
                                
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                                finish();
                                
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
    
    private void updateDatabaseWithUser(FirebaseUser mCurrentUser) {
        ModelUser user = new ModelUser(mCurrentUser.getDisplayName(), String.valueOf(mCurrentUser.getPhotoUrl())
                , "20", null, null, null);
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(mCurrentUser.getUid());
        
        reference.setValue(user);
    }
    
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    private void ManualFix() {
    
        /*
        update user's profile first
         */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                Log.e(TAG, "THe providerID is " + user.getProviderId());
                if (user.getProviderId().equals("google.com")) {
                    String modifiedImageUrl = null;
                    assert currentUser != null;
                    if (currentUser.getPhotoUrl() != null) {
                        String url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                        modifiedImageUrl = url.replace("/s96-c/", "/s300-c/");
                    }
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(modifiedImageUrl))
                            .build();
                    currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "User profile updated.");
                                    }
                                }
                            });
                } else if (user.getProviderId().equals("facebook.com")) {
                    String modifiedImageUrl = null;
                    String facebookUserId = "";
                    // find the Facebook profile and get the user's id
                    for (UserInfo profile : currentUser.getProviderData()) {
                        // check if the provider id matches "facebook.com"
                        if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                            facebookUserId = profile.getUid();
                        }
                    }
                    // construct the URL to the profile picture, with a custom height
                    // alternatively, use '?type=small|medium|large' instead of ?height=
                    String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
                    if (currentUser.getPhotoUrl() != null) {
                        modifiedImageUrl = photoUrl;
                        Log.e(TAG, "The photo url is " + modifiedImageUrl);
                    }
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(modifiedImageUrl))
                            .build();
                    currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "User profile updated.");
                                    }
                                }
                            });
                }
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
        if (!loggedIn)
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
        Log.e(TAG, "Logged via facebook " + loggedIn);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    
    private void handleSignInResult(final GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            progressDialog = new ProgressDialog(SignIn.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.loading_message));
            progressDialog.setCancelable(false);
            progressDialog.show();
            // Toast.makeText(LogInEmail.this , "The information received by the account is " + account.getDisplayName() , Toast.LENGTH_LONG).show();
            final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                    
                                    /*
                            Call manual fix to update the photo of user
                             */
                                
                                ManualFix();
                                
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Boolean isnewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                Log.e(TAG, "The user is a new user or not " + isnewUser);
                                //   checkAndUpdateUserInfo(user);
                                FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                                if (isnewUser) {
                                    /*
                                    move to setup the account/profile
                                     */
                                    updateDatabaseWithUser(mCurrentUser);
                                    startActivity(new Intent(SignIn.this, MainActivity.class));
                                    finish();
                                    
                                } else {
                                    /*
                                    move to main activity
                                     */
                                    
                                    startActivity(new Intent(SignIn.this, MainActivity.class));
                                    finish();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.cancel();
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(SignIn.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
    
    @Override
    protected void onStop() {
        if (authStateListener != null)
            mAuth.removeAuthStateListener(authStateListener);
        super.onStop();
    }
    
}

