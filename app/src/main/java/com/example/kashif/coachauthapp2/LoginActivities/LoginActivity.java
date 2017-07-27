package com.example.kashif.coachauthapp2.LoginActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kashif.coachauthapp2.HomeScreenActivity.MainActivity;
import com.example.kashif.coachauthapp2.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 1 ;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    private com.google.android.gms.common.SignInButton signInButton;
    private CallbackManager mCallbackManager;

    private ProgressDialog progressDialog;
    LinearLayout loginButtonsLayout;
    private ImageView facebook_login_imageView;

    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        facebook_login_imageView = (ImageView) findViewById(R.id.facebook_login_imageView);

        signInButton = (com.google.android.gms.common.SignInButton)findViewById(R.id.sign_in_button);
        loginButtonsLayout = (LinearLayout) findViewById(R.id.login_buttons_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in..");


        // getting the selected login choice from user
        Intent intent = getIntent();
        final String selectedLoginChoice = intent.getExtras().getString("SelectedLoginChoice");



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                signIn();
            }
        });


        // facebook signIn code here

        //facebook_login_button = (LoginButton) findViewById(R.id.facebook_login_button);

        mCallbackManager = CallbackManager.Factory.create();
        facebook_login_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        showProgressDialog();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // checking if user is already signed in.

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    goToUserProfileScreen(user, selectedLoginChoice);
                    finish();

                }
            }
        };


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                showProgressDialog();

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "" + task.getException(), Toast.LENGTH_LONG).show();
                            LoginManager.getInstance().logOut();
                            hideProgressDialog();
                        }
                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void goToUserProfileScreen(FirebaseUser user, String selectedLoginChoice){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra("SelectedLoginChoice", selectedLoginChoice);
        intent.putExtra("uniqueregid", user.getUid());
        intent.putExtra("username",user.getDisplayName());
        if (user.getEmail() == null){
            intent.putExtra("usermail","-");
        }
        else {
            intent.putExtra("usermail",user.getEmail());
        }
        intent.putExtra("userimageurl",user.getPhotoUrl().toString());
        startActivity(intent);
    }

    // method to show progress dialog while signing in
    public void showProgressDialog(){
        loginButtonsLayout.setVisibility(View.GONE);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    // method to show progress dialog
    public void hideProgressDialog(){
        progressDialog.hide();
        loginButtonsLayout.setVisibility(View.VISIBLE);
    }

    // method to all loggedIn google accounts while signingIn
    public  void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }
}
