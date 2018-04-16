package com.example.hadar.exercise02;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
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
import com.google.firebase.auth.GoogleAuthProvider;
import pl.droidsonroids.gif.GifTextView;

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";
    public static final String GOOGLE_URL_PATH_TO_REMOVE = "s96-c/photo.jpg";
    public static final String GOOGLE_URL_PATH_TO_ADD = "s400-c/photo.jpg";
    private static int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth m_firebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthListener;
    private EditText m_userEmail;
    private EditText m_userPassword;
    private CallbackManager m_callbackManager;
    private AccessTokenTracker m_accessTokenTracker;
    private GoogleSignInClient m_googleSignInClient;
    private SignInButton m_googleSignInButton;
    private LoginButton m_facebookLoginButton;
    private UserDetails m_userDetails;
    private GifTextView m_LoadingBar;

    private boolean m_googleSignedIn=false, m_faceboolSignedIn=false;
    private GifTextView m_googleLoadingBar;
    private FirebaseUser m_firebaseUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_firebaseAuth = FirebaseAuth.getInstance();
        m_LoadingBar=findViewById(R.id.load_bar);

        findViews();
        facebookLoginInit();
        googleSignInInit();
        firebaseAuthenticationInit();
    }

    public void findViews()
    {
        m_googleLoadingBar=findViewById(R.id.google_load_bar);
        m_userEmail = findViewById(R.id.editTextEmail);
        m_userPassword = findViewById(R.id.editTextPassword);
        m_googleSignInButton = findViewById(R.id.google_sign_in_button);
        m_facebookLoginButton = findViewById(R.id.buttonFacebook);
    }

    public void googleSignInInit()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        m_googleSignInClient = GoogleSignIn.getClient(this, gso);

        m_googleSignInButton.setOnClickListener(new View.OnClickListener()
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
                    public void onClick(DialogInterface i_dialogInterface, int i_num)
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
        m_googleSignedIn=true;
        m_faceboolSignedIn=false;

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int i_requestCode, int i_resultCode, Intent i_dataIntent)
    {
        super.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        m_callbackManager.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        if (i_requestCode == GOOGLE_SIGN_IN)
        {
            playGif();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(i_dataIntent);
            handleGoogleSignInResult(task);
        }
        else
        {
            m_googleSignedIn=false;
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        Log.e(TAG, "handleSignInResult() >>");

        try
        {
            GoogleSignInAccount account= completedTask.getResult(ApiException.class);
            Log.e(TAG, "firebase <= google");
            firebaseAuthWithGoogle(account);
        }

        catch (ApiException e)
        {
            Log.e(TAG, e.toString());
        }

        Log.e(TAG, "handleSignInResult() <<");
    }

    public void playGif() //plays google loading animation
    {
        final Animation Loader = new AlphaAnimation(1.f, 1.f);
        Log.e(TAG, "play gif >> "+ m_faceboolSignedIn);
        Loader.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e(TAG, "google login anim= "+m_googleSignedIn );
                Log.e(TAG, "facebook login anim= "+m_googleSignedIn );

                if (m_googleSignedIn == true)
                {
                    m_LoadingBar.setBackgroundResource(R.drawable.google_dark_load);
                    //m_LoadingBar.setX(460);
                    //m_LoadingBar.setY(1650);
                }

                if(m_faceboolSignedIn==true) {
                    m_LoadingBar.setBackgroundResource(R.drawable.facebook_load_anim);
                    //m_LoadingBar.setX(460);
                    //m_LoadingBar.setY(1200);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                m_LoadingBar.clearAnimation();
            }
        });

        m_googleLoadingBar.startAnimation(googleLoader);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount i_googleSignInAccount)
    {
        Log.e(TAG, "firebaseAuthWithGoogle() >>, id = " + i_googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(i_googleSignInAccount.getIdToken(), null);

        m_firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            m_firebaseAuth.getCurrentUser().updateEmail(i_googleSignInAccount.getEmail());
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "calling updateUI 2 " + m_firebaseAuth.getCurrentUser().getEmail());

                            Toast.makeText(MainActivity.this, "Google sign in success!", Toast.LENGTH_SHORT).show();
                            handleAllSignInSuccess("Google");
                        }

                        else
                        {
                            Toast.makeText(MainActivity.this, "Google sign in error :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Log.e(TAG, "firebaseAuthWithGoogle() <<");
    }

    private void updateUIAndMoveToUserDetailsActivity()
    {
        if(m_firebaseUser != null)
        {
            Intent userDetailsIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            userDetailsIntent.putExtra("User Details", m_userDetails);
            startActivity(userDetailsIntent);
            finish();
        }
    }

    public void handleAllSignInSuccess(String i_loginMethod)
    {
        m_firebaseUser = m_firebaseAuth.getCurrentUser();
        createUserDetailsFromFirebaseUser();
        overrideUserDetailsPictureUrl(i_loginMethod);
        updateUIAndMoveToUserDetailsActivity();
    }

    public void overrideUserDetailsPictureUrl(String i_loginMethod)
    {
        switch (i_loginMethod)
        {
            case "Google":
                changeUserDetailsPictureUrlForGoogle(GOOGLE_URL_PATH_TO_REMOVE, GOOGLE_URL_PATH_TO_ADD);
                break;

            case "Facebook":
                changeUserDetailsPictureUrlForFacebook();
                break;

            case "EmailPassword":
                //Check if email / password quality is good enough *****************************************
                //If it is, remove this case from switch ************************************************************
                // ******************************************************************************************
                break;

            default:
                return;
        }
    }

    public void changeUserDetailsPictureUrlForFacebook()
    {
        String newPicturePath = Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString();

        m_userDetails.setUserPictureUrl(newPicturePath);
    }

    public void changeUserDetailsPictureUrlForGoogle(String i_originalPieceOfUrlToRemove, String i_newPieceOfUrlToAdd)
    {
        String userDetailsPictureUrl = m_userDetails.getUserPictureUrl();

        if(userDetailsPictureUrl != null)
        {
            String newPicturePath = userDetailsPictureUrl.replace(i_originalPieceOfUrlToRemove, i_newPieceOfUrlToAdd);
            m_userDetails.setUserPictureUrl(newPicturePath);
        }
    }

    public void createUserDetailsFromFirebaseUser()
    {
        if(m_firebaseUser != null)
        {
            m_userDetails = new UserDetails(m_firebaseUser);
        }
    }

    private void facebookLoginInit()
    {
        Log.e(TAG, "facebookLoginInit() >>");
        m_callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebook);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        m_facebookLoginButton.setReadPermissions("email", "public_profile", "user_friends");

        m_facebookLoginButton.registerCallback(m_callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult i_loginResult)
            {
                Toast.makeText(MainActivity.this, "Facebook sign in success!", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(i_loginResult.getAccessToken());
                m_faceboolSignedIn=true;
                m_googleSignedIn=false;
            }

            @Override
            public void onCancel()
            {
                Toast.makeText(MainActivity.this, "Facebook sign in canceled", Toast.LENGTH_SHORT).show();
                m_faceboolSignedIn=false;
            }

            @Override
            public void onError(FacebookException i_facebookException)
            {
                Toast.makeText(MainActivity.this, "Facebook sign in error :(", Toast.LENGTH_SHORT).show();
            }
        });

        m_accessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken i_oldAccessToken, AccessToken i_currentAccessToken)
            {
                if (i_currentAccessToken == null)
                {
                    m_firebaseAuth.signOut();
                }

                Log.e(TAG, "onCurrentAccessTokenChanged() >> currentAccessToken=" +
                        (i_currentAccessToken != null ? i_currentAccessToken.getToken() : "Null") +
                        " ,oldAccessToken=" +
                        (i_oldAccessToken != null ? i_oldAccessToken.getToken() : "Null"));
            }
        };

        Log.e(TAG, "facebookLoginInit() <<");
    }

    private void handleFacebookAccessToken(AccessToken i_accessToken)
    {
        Log.e(TAG, "handleFacebookAccessToken () >>" + i_accessToken.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(i_accessToken.getToken());
        m_firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.e(TAG, "Facebook: onComplete() >> " + task.isSuccessful());

                        if(task.isSuccessful())
                        {
                          handleAllSignInSuccess("Facebook");
                        }

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
            updateUIAndMoveToUserDetailsActivity();

        Log.e(TAG, "onStart() <<");
    }

    @Override
    protected void onStop()
    {
        Log.e(TAG, "onStop() >>");

        super.onStop();

        if (m_AuthListener != null)
        {
            m_firebaseAuth.removeAuthStateListener(m_AuthListener);
        }

        Log.e(TAG, "onStop() <<");
    }

    private void firebaseAuthenticationInit()
    {
        Log.e(TAG, "firebaseAuthenticationInit() >>");

        //Obtain reference to the current authentication
        m_firebaseAuth = FirebaseAuth.getInstance();

        m_AuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
            }
        };

        Log.e(TAG, "firebaseAuthenticationInit() <<");
    }

    public void OnClickForgotPassword(View i_view)
    {
        Log.e(TAG, "OnClickForgotPassword() >> ");

        String emailStr = m_userEmail.getText().toString();

        if(emailStr.isEmpty())
            Toast.makeText(MainActivity.this, "Please type an email address in order to reset your password.", Toast.LENGTH_LONG).show();
        else
        {
            m_firebaseAuth.sendPasswordResetEmail(emailStr)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> i_completedTask)
                        {
                            Log.e(TAG, "OnClickForgotPassword: onComplete() >> " + i_completedTask.isSuccessful());

                            if (i_completedTask.isSuccessful())
                            {
                                showPasswordResetWasSentDialog();
                            }

                            else
                            {
                                Toast.makeText(MainActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                            Log.e(TAG, "OnClickForgotPassword: onComplete() << " + i_completedTask.isSuccessful());
                        }
                    });
        }

        Log.e(TAG, "OnClickForgotPassword() << ");
    }

    private void emailAndPasswordValidation ()throws Exception
    {
        if(m_userEmail.getText().toString().matches(""))
        {
            throw new Exception("Email field is empty");
        }

        else if(m_userPassword.getText().toString().matches(""))
        {
            throw new Exception("Password field is empty");
        }
    }

    public void onSignInClick(View i_view)
    {
        Log.e(TAG, "onSignInClick() >>");

        try
        {
            emailAndPasswordValidation();
            String email = m_userEmail.getText().toString();
            String pass = m_userPassword.getText().toString();

            //Email / Password sign-in
            Task<AuthResult> authResult = m_firebaseAuth.signInWithEmailAndPassword(email, pass);

            authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> i_completedTask)
                {

                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + i_completedTask.isSuccessful());

                    if (!i_completedTask.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        if (m_firebaseAuth.getCurrentUser().isEmailVerified())
                        {
                            Log.e(TAG, "calling updateUI 5");
                            updateUI(m_firebaseAuth.getCurrentUser());
                            handleAllSignInSuccess("EmailPassword");
                        }

                        else
                        {
                            showWaitingForEmailVerificationDialog();
                            m_firebaseAuth.signOut();
                        }
                    }

                    Log.e(TAG, "Email/Pass Auth: onComplete() <<");
                }
            });
        }

        catch (Exception exception)
        {
            Log.e(TAG, "emailAndPasswordValidation"+ exception.getMessage());

            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }

            Log.e(TAG, "onSignInClick() <<");
    }

    public void onSignUp(View i_view)
    {
        Intent regIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        regIntent.putExtra("Email", m_userEmail.getText().toString());
        startActivity(regIntent);
    }

    public void showWaitingForEmailVerificationDialog()
    {
        new AlertDialog.Builder(this)
                .setMessage("Waiting for email verification.\nPlease verify your account and sign in again.\n")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    public void showPasswordResetWasSentDialog()
    {
        new AlertDialog.Builder(this)
                .setMessage("A password reset request has been sent to:\n" + m_userEmail.getText().toString())
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }
}