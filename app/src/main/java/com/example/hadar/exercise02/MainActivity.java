package com.example.hadar.exercise02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";
    private static int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmail;
    private EditText mPass;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        facebookLoginInit();
        firebaseAuthenticationInit();
        mEmail = findViewById(R.id.editTextEmail);
        mPass = findViewById(R.id.editTextPassword);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onClickGoogleButton();
            }
        });
    }

    public void onClickGoogleButton()
    {
        Toast.makeText(MainActivity.this, "Google",Toast.LENGTH_SHORT).show();
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        }
        catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        Intent userDetails = new Intent(getApplicationContext(), UserDetailsActivity.class);
        userDetails.putExtra("Google Account", account);
        startActivity(userDetails);
    }

    private void linkForgotPassword()
    {
        //Forgot password test
        TextView t2 = (TextView) findViewById(R.id.textViewForgotPassword);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void OnClickForgotPassword(View v)
    {
        int i=3;
        i++;
    }

    private void facebookLoginInit() {


        Log.e(TAG, "facebookLoginInit() >>");

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess () >>" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG, "facebook:onSuccess () <<");
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel() >>");
              //  updateLoginStatus("Facebook login canceled");
                Log.e(TAG, "facebook:onCancel() <<");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError () >>" + error.getMessage());
               // updateLoginStatus(error.getMessage());
                Log.e(TAG, "facebook:onError <<");
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                   // updateLoginStatus("Facebook signuout");
                }
                Log.e(TAG,"onCurrentAccessTokenChanged() >> currentAccessToken="+
                        (currentAccessToken !=null ? currentAccessToken.getToken():"Null") +
                        " ,oldAccessToken=" +
                        (oldAccessToken !=null ? oldAccessToken.getToken():"Null"));

            }
        };
        Log.e(TAG, "facebookLoginInit() <<");
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Log.e(TAG, "handleFacebookAccessToken () >>" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "Facebook: onComplete() >> " + task.isSuccessful());

                        //updateLoginStatus(task.isSuccessful() ? "N.A" : task.getException().getMessage());

                        Log.e(TAG, "Facebook: onComplete() <<");
                    }
                });

        Log.e(TAG, "handleFacebookAccessToken () <<");

    }

    @Override
    protected void onStart() {

        Log.e(TAG, "onStart() >>");

        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //  updateLoginStatus("N.A");

        Log.e(TAG, "onStart() <<");

    }

    @Override
    protected void onStop() {

        Log.e(TAG, "onStop() >>");

        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.e(TAG, "onStop() <<");

    }

    private void firebaseAuthenticationInit() {

        Log.e(TAG, "firebaseAuthenticationInit() >>");
        //Obtain reference to the current authentication
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e(TAG, "onAuthStateChanged() >>");

              //  updateLoginStatus("N.A");

                Log.e(TAG, "onAuthStateChanged() <<");
            }
        };

        Log.e(TAG, "firebaseAuthenticationInit() <<");
    }

    public void onEmailPasswordAuthClick(View V)
    {
        Log.e(TAG, "onEmailPasswordAuthClick() >>");

        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        Task<AuthResult> authResult;

        switch (V.getId()) {
            case R.id.buttonSignIn:
                //Email / Password sign-in
                authResult = mAuth.signInWithEmailAndPassword(email, pass);
                break;
            case R.id.buttonSignUP:
                //Email / Password sign-up
                authResult = mAuth.createUserWithEmailAndPassword(email, pass);
                break;
            default:
                return;
        }
        authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.isSuccessful());
                String mas = "success";
                if(!task.isSuccessful())
                     mas = task.getException().getMessage();

                //updateLoginStatus(task.isSuccessful() ? "N.A" : task.getException().getMessage());
                Toast.makeText(MainActivity.this, mas, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Email/Pass Auth: onComplete() <<");
            }
        });

        Log.e(TAG, "onEmailPasswordAuthClick() <<");
    }



}
