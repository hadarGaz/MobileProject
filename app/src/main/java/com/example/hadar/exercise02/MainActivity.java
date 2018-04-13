package com.example.hadar.exercise02;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import pl.droidsonroids.gif.GifTextView;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";
    private static int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth m_firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmail;
    private EditText mPass;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;
    private GoogleSignInClient m_googleSignInClient;
    private UserDetails m_userDetails;
    private GifTextView m_googleLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_firebaseAuth = FirebaseAuth.getInstance();

        m_googleLoadingBar=findViewById(R.id.google_load_bar);

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
        m_googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickGoogleButton();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
                        exitIntent.addCategory(Intent.CATEGORY_HOME);
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(exitIntent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onClickGoogleButton()
    {
        Intent signInIntent = m_googleSignInClient.getSignInIntent();
        playGif();

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
           // GoogleSignInAccount googleSignInAccount = completedTask.getResult(ApiException.class);
            GoogleSignInAccount account= completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        }

        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }



    public void playGif(){
        final Animation googleLoader = new AlphaAnimation(1.f, 1.f);
        //googleLoader.setDuration(20000);
        googleLoader.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation)
            {

                m_googleLoadingBar.setBackgroundResource(R.drawable.google_dark_load);
            }

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                m_googleLoadingBar.clearAnimation();
            }
        });
        m_googleLoadingBar.startAnimation(googleLoader);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        m_firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
          //                  Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = m_firebaseAuth.getCurrentUser();
                        //    updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
            //                Log.w(TAG, "signInWithCredential:failure", task.getException());
              //              Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser i_firebaseUser)
    {
        if(i_firebaseUser != null)
        {
            Intent userDetails = new Intent(getApplicationContext(), UserDetailsActivity.class);
            m_userDetails = new UserDetails(i_firebaseUser);
            userDetails.putExtra("User Details", m_userDetails);
            startActivity(userDetails);
           // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
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
                    m_firebaseAuth.signOut();
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
        m_firebaseAuth.signInWithCredential(credential)
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

        m_firebaseAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = m_firebaseAuth.getCurrentUser();

        //  updateLoginStatus("N.A");

        Log.e(TAG, "onStart() <<");

    }

    @Override
    protected void onStop() {

        Log.e(TAG, "onStop() >>");

        super.onStop();

        if (mAuthListener != null) {
            m_firebaseAuth.removeAuthStateListener(mAuthListener);
        }
        Log.e(TAG, "onStop() <<");

    }

    private void firebaseAuthenticationInit() {

        Log.e(TAG, "firebaseAuthenticationInit() >>");
        //Obtain reference to the current authentication
        m_firebaseAuth = FirebaseAuth.getInstance();

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
                authResult = m_firebaseAuth.signInWithEmailAndPassword(email, pass);
                break;
            case R.id.buttonSignUP:
                //Email / Password sign-up
                authResult = m_firebaseAuth.createUserWithEmailAndPassword(email, pass);
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
                updateUI(m_firebaseAuth.getCurrentUser());
                Log.e(TAG, "Email/Pass Auth: onComplete() <<");
            }
        });

        Log.e(TAG, "onEmailPasswordAuthClick() <<");
    }



}
