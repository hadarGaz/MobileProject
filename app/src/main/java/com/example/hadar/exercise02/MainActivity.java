package com.example.hadar.exercise02;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";
    //public static final int CLEAR_ANIMATION = 0;
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
    private UserDetails m_userDetails;
    //private GifTextView m_LoadingBar;
    private GoogleSignInAccount m_googleSignInAccount;
    //private boolean m_googleSignedIn = false, m_facebookSignedIn = false;
    private FirebaseUser m_firebaseUser = null;
    private FirebaseRemoteConfig m_FirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_main);

        m_firebaseAuth = FirebaseAuth.getInstance();
        GifPlayer.m_LoadingBar=findViewById(R.id.load_bar);
        m_FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        m_LoadingBar=findViewById(R.id.load_bar);

        findViews();
        facebookLoginInit();
        googleSignInInit();
        firebaseAuthenticationInit();
    }

    public void findViews()
    {
        //m_googleLoadingBar=findViewById(R.id.google_load_bar);
        m_userEmail = findViewById(R.id.editTextEmail);
        m_userPassword = findViewById(R.id.editTextPassword);
        m_googleSignInButton = findViewById(R.id.google_sign_in_button);
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
                GifPlayer.setGoogleSignIn(true);
                GifPlayer.playGif();
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
        GifPlayer.playGif();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int i_requestCode, int i_resultCode, Intent i_dataIntent)
    {
        super.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        m_callbackManager.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        if (i_requestCode == GOOGLE_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(i_dataIntent);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> i_completedTask)
    {
        Log.e(TAG, "handleSignInResult() >>");

        try
        {
            m_googleSignInAccount = i_completedTask.getResult(ApiException.class);
            Log.e(TAG, "firebase <= google");
            firebaseAuthWithGoogle(m_googleSignInAccount);
        }

        catch (ApiException e)
        {

            GifPlayer.stopGif();

            Log.e(TAG, "unsuccessful sign in to google");
        }

        Log.e(TAG, "handleSignInResult() <<");
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
        overrideUserDetailsInformation(i_loginMethod);
        updateUIAndMoveToUserDetailsActivity();
    }

    public void overrideUserDetailsInformation(String i_loginMethod)
    {
        switch (i_loginMethod)
        {
            case "Google":
                changeUserDetailsPictureUrlForGoogle(GOOGLE_URL_PATH_TO_REMOVE, GOOGLE_URL_PATH_TO_ADD);
                m_userDetails.setUserEmail(m_googleSignInAccount.getEmail().toString());
                break;

            case "Facebook":
                changeUserDetailsPictureUrlForFacebook(m_userDetails);
                setUserEmailToFacebookUser(m_userDetails, m_firebaseUser);
                break;
            case "Anonymously":
                break;
            default:
                return;
        }
    }

    public static void setUserEmailToFacebookUser(UserDetails i_userDetails, FirebaseUser i_firebaseUser)
    {
        for (UserInfo userInfo: i_firebaseUser.getProviderData())
        {
            if(userInfo.getProviderId().equals("facebook.com"))
            {
                i_userDetails.setUserEmail(userInfo.getEmail());
            }
        }
    }

    public static void changeUserDetailsPictureUrlForFacebook(UserDetails i_userDetails)
    {
        Profile facebookProfile = Profile.getCurrentProfile();

        if(facebookProfile != null)
        {
            String newPicturePath = facebookProfile.getProfilePictureUri(500, 500).toString();

            i_userDetails.setUserPictureUrl(newPicturePath);
        }
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
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                GifPlayer.setFacebookSignIn(true);
                GifPlayer.playGif();
            }
        });

        loginButton.registerCallback(m_callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult i_loginResult)
            {
                Toast.makeText(MainActivity.this, "Facebook sign in success!", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(i_loginResult.getAccessToken());


            }

            @Override
            public void onCancel()
            {
                Toast.makeText(MainActivity.this, "Facebook sign in canceled", Toast.LENGTH_SHORT).show();
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
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.e(TAG, "Facebook: onComplete() >> " + task.isSuccessful());

                if (task.isSuccessful())
                {
                    handleAllSignInSuccess("Facebook");
                }

                else
                    {
                    GifPlayer.stopGif();
                    Log.e(TAG, "unsuccessful sign in to google");
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

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
                    } else
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

                    if (i_completedTask.isSuccessful())
                    {
                        if (m_firebaseAuth.getCurrentUser().isEmailVerified())
                        {
                            Log.e(TAG, "calling updateUI 5");
                            updateUIAndMoveToUserDetailsActivity();
                            handleAllSignInSuccess("EmailPassword");
                        }

                        else
                        {
                            showWaitingForEmailVerificationDialog();
                            m_firebaseAuth.signOut();
                        }
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
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

    public void onSignUpAnonymouslyClick(View v)
    {
        long cacheExpiration =0;
        m_FirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            m_FirebaseRemoteConfig.activateFetched();

                            if(m_FirebaseRemoteConfig.getBoolean("allow_annoymous_user") == true)
                            {
                                signUpAnonymously();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Not allow anonymous user",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUpAnonymously()
    {
        m_firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = m_firebaseAuth.getCurrentUser();
                            updateProfile();
                        }
                        else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });


    }
    private void updateProfile()
    {
        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName("Anonymously")
                .build();

        m_firebaseAuth.getCurrentUser().updateProfile(updateProfile)
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                            handleAllSignInSuccess("Anonymously");
                    }
                });
    }


}