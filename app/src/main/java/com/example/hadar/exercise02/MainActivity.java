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

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";
    private static int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth m_firebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthListener;
    private EditText m_userEmailEditText;
    private EditText m_userPasswordEditText;
    private CallbackManager m_callbackManager;
    private AccessTokenTracker m_accessTokenTracker;
    private GoogleSignInClient m_googleSignInClient;
    private UserDetails m_userDetails;
    private GifTextView m_googleLoadingBar;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_main);
        m_firebaseAuth = FirebaseAuth.getInstance();

        m_googleLoadingBar=findViewById(R.id.google_load_bar);

        facebookLoginInit();
        firebaseAuthenticationInit();
        m_userEmailEditText = findViewById(R.id.editTextEmail);
        m_userPasswordEditText = findViewById(R.id.editTextPassword);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
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
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int i_requestCode, int i_resultCode, Intent i_dataIntent)
    {
        super.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        m_callbackManager.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (i_requestCode == GOOGLE_SIGN_IN)
        {
            playGif();
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(i_dataIntent);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> i_completedTask)
    {
        try
        {
           // GoogleSignInAccount googleSignInAccount = completedTask.getResult(ApiException.class);
            GoogleSignInAccount account= i_completedTask.getResult(ApiException.class);
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

    public void playGif() //plays google loading animation
    {
        final Animation googleLoader = new AlphaAnimation(1.f, 1.f);

        googleLoader.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                m_googleLoadingBar.setBackgroundResource(R.drawable.google_dark_load);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                m_googleLoadingBar.clearAnimation();
            }
        });

        m_googleLoadingBar.startAnimation(googleLoader);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount i_googleSignInAccount)
    {
        Log.e(TAG, "firebaseAuthWithGoogle:" + i_googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(i_googleSignInAccount.getIdToken(), null);

        m_firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI(m_firebaseAuth.getCurrentUser());
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void linkForgotPassword()
    {
        //Forgot password test
        TextView t2 = (TextView) findViewById(R.id.textViewForgotPassword);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void OnClickForgotPassword(View i_view)
    {
        int i=3;
        i++;
    }

    private void facebookLoginInit()
    {
        Log.e(TAG, "facebookLoginInit() >>");

        m_callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebook);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        loginButton.registerCallback(m_callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(MainActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "facebook:onSuccess () >>" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG, "facebook:onSuccess () <<");
            }

            @Override
            public void onCancel()
            {
                Log.e(TAG, "facebook:onCancel() >>");
                //  updateLoginStatus("Facebook login canceled");
                Log.e(TAG, "facebook:onCancel() <<");

            }

            @Override
            public void onError(FacebookException error)
            {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "facebook:onError () >>" + error.getMessage());
                // updateLoginStatus(error.getMessage());
                Log.e(TAG, "facebook:onError <<");
            }
        });

        m_accessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                if (currentAccessToken == null)
                {
                    m_firebaseAuth.signOut();
                    // updateLoginStatus("Facebook signuout");
                }

                Log.e(TAG, "onCurrentAccessTokenChanged() >> currentAccessToken=" +
                        (currentAccessToken != null ? currentAccessToken.getToken() : "Null") +
                        " ,oldAccessToken=" +
                        (oldAccessToken != null ? oldAccessToken.getToken() : "Null"));

            }
        };

        Log.e(TAG, "facebookLoginInit() <<");
    }

    private void handleFacebookAccessToken(AccessToken i_accessToken)
    {
        Log.e(TAG, "handleFacebookAccessToken () >>" + i_accessToken.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(i_accessToken.getToken());
        m_firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.e(TAG, "Facebook: onComplete() >> " + task.isSuccessful());

                        if(task.isSuccessful())
                            updateUI(m_firebaseAuth.getCurrentUser());
                        else
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "Facebook: onComplete() <<");
                    }
                });

        Log.e(TAG, "handleFacebookAccessToken () <<");
    }

    @Override
    protected void onStart()
    {
        Log.e(TAG, "onStart() >>");

        super.onStart();

        m_firebaseAuth.addAuthStateListener(m_AuthListener);
        FirebaseUser currentUser = m_firebaseAuth.getCurrentUser();
        if(currentUser!= null)
            updateUI(currentUser);

        Log.e(TAG, "onStart() <<");
    }

    @Override
    protected void onStop()
    {

        Log.e(TAG, "onStop() >>");

        super.onStop();

        if (m_AuthListener != null) {
            m_firebaseAuth.removeAuthStateListener(m_AuthListener);
        }
        Log.e(TAG, "onStop() <<");
    }

    private void firebaseAuthenticationInit()
    {
        Log.e(TAG, "firebaseAuthenticationInit() >>");
        //Obtain reference to the current authentication
        m_firebaseAuth = FirebaseAuth.getInstance();

        m_AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e(TAG, "onAuthStateChanged() >>");

              //  updateLoginStatus("N.A");

                Log.e(TAG, "onAuthStateChanged() <<");
            }
        };

        Log.e(TAG, "firebaseAuthenticationInit() <<");
    }

    private boolean emailAndPasswordValidation ()
    {
        return !(m_userEmailEditText.getText().toString().matches("") || m_userPasswordEditText.getText().toString().matches(""));
    }

    public void onEmailPasswordAuthClick(View i_view)
    {
        Log.e(TAG, "onEmailPasswordAuthClick() >>");

        if(emailAndPasswordValidation()== false)
        {
            Log.e(TAG, "Invalid Email or Password");
            Toast.makeText(this, "Invalid User Name or Password",Toast.LENGTH_LONG).show();
        }

        else
        {
            String email = m_userEmailEditText.getText().toString();
            String pass = m_userPasswordEditText.getText().toString();

            //Email / Password sign-in
            Task<AuthResult> authResult = m_firebaseAuth.signInWithEmailAndPassword(email, pass);

            authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {

                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.isSuccessful());
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else
                    {
                        if (m_firebaseAuth.getCurrentUser().isEmailVerified())
                        {
                            updateUI(m_firebaseAuth.getCurrentUser());
                        } else
                        {
                            Toast.makeText(MainActivity.this, "Email wasn't verified yet", Toast.LENGTH_SHORT).show();
                            m_firebaseAuth.signOut();
                        }
                    }
                    Log.e(TAG, "Email/Pass Auth: onComplete() <<");
                }
            });

            Log.e(TAG, "onEmailPasswordAuthClick() <<");
        }
    }

    public void onSignUp(View i_view)
    {
        Intent regIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        regIntent.putExtra("Email", m_userEmailEditText.getText().toString());
        startActivity(regIntent);
    }
}