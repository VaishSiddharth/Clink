package com.testlabic.datenearu.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.NewUserSetupUtils.NewUserSetup;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;
import com.testlabic.datenearu.Utils.Utils;
import com.testlabic.datenearu.WaveDrawable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.core.content.ContextCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 48;
    private static final String TAG = SignIn.class.getSimpleName();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    LinearLayout googleSignIn;
    LinearLayout facebookSignIn;
    GoogleProgressBar progressBar;
    CallbackManager callbackManager;
    LoginButton loginButton;
    SweetAlertDialog loadingDialog;
    ImageView imageView;
    int verCode;
    //ImageView applogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setupWindowAnimations();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading")
                .setContentText("Wait a while...");

        imageView=findViewById(R.id.test);
        //applogo = findViewById(R.id.applogo);
        WaveDrawable mWaveDrawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mWaveDrawable = new WaveDrawable(getDrawable(R.drawable.nohere));
        }
        if (mWaveDrawable != null) {
            mWaveDrawable.setWaveAmplitude(30);
            mWaveDrawable.setWaveLength(580);
            mWaveDrawable.setWaveSpeed(12);
            mWaveDrawable.setIndeterminate(true);
        }
       
        //((WaveDrawable) mWaveDrawable).setLevel(20);
       
        imageView.setImageDrawable(mWaveDrawable);
        progressBar = findViewById(R.id.google_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        /*progressBar = findViewById(R.id.google_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
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
            }
        });
        
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "photos", "user_photos");
        // If using in a fragment
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                if (loginResult != null) {
                    Profile profile = Profile.getCurrentProfile();
                    handleFacebookAccessToken(loginResult.getAccessToken(), profile);
                }
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
    public void appUpdates() {
        final String appPackageName = this.getPackageName();// getPackageName() from Context or Activity object
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionCode;
            String version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //Log.e(TAG,"Version in signIn "+verCode);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("appVersion");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String versionCode = dataSnapshot.getValue(String.class);
                String verCodestr = String.valueOf(verCode);
                //Log.e(TAG,"Version in signIn db "+versionCode);
                if (!verCodestr.equalsIgnoreCase(versionCode)) {
                    //Log.e(TAG,"Version in signIn db "+versionCode);
                    SweetAlertDialog alertDialog = new SweetAlertDialog(SignIn.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("App needs Update !!")
                            .setContentText("Some new features are added & bugs are fixed please update the app otherwise the app will crash frequently.\n" +
                                    "If this doesn't works please uninstall and reinstall CLINK\n")
                            .setConfirmButton("Update", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                }
                            });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button btn = alertDialog.findViewById(R.id.confirm_button);
                    btn.setBackground(ContextCompat.getDrawable(SignIn.this, R.drawable.button_4_dialogue));
                    Button btn1 = alertDialog.findViewById(R.id.cancel_button);
                    btn1.setBackground(ContextCompat.getDrawable(SignIn.this, R.drawable.button_4_dialogue));

                    {
                        btn.setTypeface(Utils.SFPRoLight(SignIn.this));
                        btn1.setTypeface(Utils.SFPRoLight(SignIn.this));

                        TextView title = alertDialog.findViewById(R.id.title_text);
                        if (title != null)
                            title.setTypeface(Utils.SFProRegular(SignIn.this));

                        TextView contentText = alertDialog.findViewById(R.id.content_text);
                        if (contentText != null)
                            contentText.setTypeface(Utils.SFPRoLight(SignIn.this));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token, final Profile profile) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        imageView.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            boolean isnewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.e(TAG, "The user is a new user or not " + isnewUser);
                            //   checkAndUpdateUserInfo(user);
                            FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                            if (isnewUser) {
                                //move to setup the account/profile
                                //Call manual fix to update the photo of user
        
                                if (mCurrentUser != null) {
                                    updateDatabaseWithUser(mCurrentUser, null, profile, false);
                                }
                                imageView.setVisibility(View.INVISIBLE);
                                //progressBar.setVisibility(View.INVISIBLE);
        
        
                            } else {
                                
                                /*imageView.setVisibility(View.INVISIBLE);
                                //progressBar.setVisibility(View.INVISIBLE);
        
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                                finish();*/
                                //startActivity(new Intent(SignIn.this, MainActivity.class).putExtra(Constants.refresh, true));
                                // finish();
    
                                if (mCurrentUser != null) {
                                    updateDatabaseWithUser(mCurrentUser, null, profile, true);
                                }
                                imageView.setVisibility(View.INVISIBLE);
                                //progressBar.setVisibility(View.INVISIBLE);
    
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            imageView.setVisibility(View.INVISIBLE);
                            //progressBar.setVisibility(View.INVISIBLE);
    
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        
                        }
                    }
                });
    }

    private void updateDatabaseWithUser(final FirebaseUser mCurrentUser, GoogleSignInAccount account, Profile profile, final Boolean fromFacebook) {

        if(loadingDialog!=null)
        loadingDialog.show();
        
        String firstName = null;
        String lastName = null;

        if(account!=null)
        {
            firstName = account.getGivenName();
            lastName = account.getFamilyName();
        }
        if(profile!=null)
        {
            firstName = profile.getFirstName();
            lastName = profile.getLastName();
        }
        HashMap<String, Object> timeStamp = new HashMap<>();
        timeStamp.put(Constants.timeStamp, ServerValue.TIMESTAMP);
        ModelUser user = new ModelUser(firstName, String.valueOf(modifiedImageUrl())
                , "NA", null, Constants.all_location, null, mCurrentUser.getUid(), lastName, -1, timeStamp, timeStamp, "ABCDEF");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.userInfo).child(mCurrentUser.getUid());

        reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // add 500 xPoints for a new user!
                HashMap<String, Object> updatePoints = new HashMap<>();
                updatePoints.put(Constants.xPoints, 1000);
    
                DatabaseReference xPointsRef = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.xPoints)
                        .child(mCurrentUser.getUid());
                xPointsRef.updateChildren(updatePoints).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(loadingDialog!=null)
                        loadingDialog.dismiss();
                        if (fromFacebook) {
                            startActivity(new Intent(SignIn.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SignIn.this, NewUserSetup.class));
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private String modifiedImageUrl() {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null&&FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()==null)
            return "";
        //update user's profile first
        String modifiedImageUrl = null;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                Log.e(TAG, "The providerID is " + user.getProviderId());
                if (user.getProviderId().equals("google.com")) {

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
                        //Log.e(TAG, "The photo url is " + modifiedImageUrl);
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
        return modifiedImageUrl;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
        if (!loggedIn)
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken(), null);
        Log.e(TAG, "Logged via facebook " + loggedIn);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    protected void onResume() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                appUpdates();
            }
        },1000);
        super.onResume();
    }

    private void handleSignInResult(final GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            //applogo.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.VISIBLE);
            // Toast.makeText(LogInEmail.this , "The information received by the account is " + account.getDisplayName() , Toast.LENGTH_LONG).show();
            final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                boolean isnewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                Log.e(TAG, "The user is a new user or not " + isnewUser);
                                //   checkAndUpdateUserInfo(user);
                                FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                                if (isnewUser) {
                                    //move to setup the account/profile
                                    //Call manual fix to update the photo of user

                                    if (mCurrentUser != null) {
                                        updateDatabaseWithUser(mCurrentUser, account, null, false);
                                    }
                                    imageView.setVisibility(View.INVISIBLE);
                                    //progressBar.setVisibility(View.INVISIBLE);
                                    

                                } else {
                                    /*
                                    move to main activity
                                     */
                                    imageView.setVisibility(View.INVISIBLE);
                                    //progressBar.setVisibility(View.INVISIBLE);

                                    startActivity(new Intent(SignIn.this, MainActivity.class));
                                    finish();
                                    //startActivity(new Intent(SignIn.this, MainActivity.class).putExtra(Constants.refresh, true));
                                    // finish();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                imageView.setVisibility(View.INVISIBLE);
                                //progressBar.setVisibility(View.INVISIBLE);

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
        finishAffinity();
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        //if (authStateListener != null)
        //   mAuth.removeAuthStateListener(authStateListener);
        super.onStop();
    }
    private void setupWindowAnimations() {
        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide();
            slide.setDuration(1000);
            getWindow().setExitTransition(slide);
        }
    }
    
}

