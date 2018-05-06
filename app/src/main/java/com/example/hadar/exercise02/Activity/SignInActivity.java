package com.example.hadar.exercise02.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.hadar.exercise02.model.GifPlayer;
import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.model.DetailsValidation;
import com.example.hadar.exercise02.model.UserDetails;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignInActivity extends Activity
{
    private static final String TAG = "SignInActivity";
    private static final String GOOGLE_URL_PATH_TO_REMOVE = "s96-c/photo.jpg";
    private static final String GOOGLE_URL_PATH_TO_ADD = "s400-c/photo.jpg";
    private String m_sourceActivity = null;
    private static int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth m_firebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthListener;
    private EditText m_userEmailEditText;
    private EditText m_userPasswordEditText;
    private CallbackManager m_callbackManager;
    private GoogleSignInClient m_googleSignInClient;
    private SignInButton m_googleSignInButton;
    private UserDetails m_userDetails;
    private GoogleSignInAccount m_googleSignInAccount;
    private FirebaseUser m_firebaseUser = null;
    private FirebaseRemoteConfig m_FirebaseRemoteConfig;
    private LoginButton m_facebookLoginButton;
    private String m_loginMethod;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        Log.e(TAG, "onCreate() >>");

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_main);

        m_sourceActivity = getIntent().getStringExtra("Source Activity");
        m_firebaseAuth = FirebaseAuth.getInstance();
        m_FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        findViews();
        facebookLoginInit();
        googleSignInInit();
        firebaseAuthenticationInit();

        Log.e(TAG, "onCreate() <<");
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

    public static void setUserEmailToFacebookUser(UserDetails i_userDetails, FirebaseUser i_firebaseUser)
    {
        Log.e(TAG, "setUserEmailToFacebookUser() >>");

        for (UserInfo userInfo: i_firebaseUser.getProviderData())
        {
            if(userInfo.getProviderId().equals("facebook.com"))
            {
                i_userDetails.setUserEmail(userInfo.getEmail());
            }
        }
        Log.e(TAG, "setUserEmailToFacebookUser() <<");

    }

    public static void changeUserDetailsPictureUrlForFacebook(UserDetails i_userDetails)
    {
        Log.e(TAG, "changeUserDetailsPictureUrlForFacebook() >>");

        Profile facebookProfile = Profile.getCurrentProfile();

        if(facebookProfile != null)
        {
            String newPicturePath = facebookProfile.getProfilePictureUri(500, 500).toString();

            i_userDetails.setUserPictureUrl(newPicturePath);
        }
        Log.e(TAG, "changeUserDetailsPictureUrlForFacebook() <<");

    }

    public static void changeUserDetailsPictureUrlForGoogle(UserDetails i_userDetails)
    {
        Log.e(TAG, "changeUserDetailsPictureUrlForGoogle() >>");

        String userDetailsPictureUrl = i_userDetails.getUserPictureUrl();

        if(userDetailsPictureUrl != null)
        {
            String newPicturePath = userDetailsPictureUrl.replace(GOOGLE_URL_PATH_TO_REMOVE, GOOGLE_URL_PATH_TO_ADD);
            i_userDetails.setUserPictureUrl(newPicturePath);
        }
        Log.e(TAG, "changeUserDetailsPictureUrlForGoogle() <<");

    }

    @Override
    public void onBackPressed()
    {
        Log.e(TAG, "onBackPressed() >>");

        if(("AskForSignInActivity").equals(m_sourceActivity))
        {
            goBackToAskForSignInActivity();
        }

        else
        {
            showExitAppDialog();
        }
        Log.e(TAG, "onBackPressed() <<");


    }

    private void goBackToAskForSignInActivity()
    {
        Log.e(TAG, "goBackToAskForSignInActivity() >>");

        super.onBackPressed();
        finish();

        Log.e(TAG, "goBackToAskForSignInActivity() <<");

    }

    private void showExitAppDialog()
    {
        Log.e(TAG, "showExitAppDialog() >>");

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
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();

        Log.e(TAG, "showExitAppDialog() <<");

    }

    @Override
    public void onActivityResult(int i_requestCode, int i_resultCode, Intent i_dataIntent)
    {
        Log.e(TAG, "onActivityResult() >>");

        super.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        m_callbackManager.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        if (i_requestCode == GOOGLE_SIGN_IN)
        {
            Task<GoogleSignInAccount> googleSignInTask = GoogleSignIn.getSignedInAccountFromIntent(i_dataIntent);
            handleGoogleSignInResult(googleSignInTask);
        }
        Log.e(TAG, "onActivityResult() <<");

    }

    @SuppressWarnings("ConstantConditions")
    public void onSignInClick(View i_view)
    {
        Log.e(TAG, "onSignInClick() >>");
        DetailsValidation detailsValidation = new DetailsValidation();
        try
        {
            detailsValidation.verifyEmail(m_userEmailEditText.getText().toString());
            detailsValidation.verifyPassword(m_userPasswordEditText.getText().toString());
            String email = m_userEmailEditText.getText().toString();
            String pass = m_userPasswordEditText.getText().toString();

            //Email / Password sign-in
            Task<AuthResult> authResult = m_firebaseAuth.signInWithEmailAndPassword(email, pass);

            authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> i_completedTask)
                {

                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + i_completedTask.isSuccessful());

                    if (i_completedTask.isSuccessful())
                    {
                        if (m_firebaseAuth.getCurrentUser().isEmailVerified())
                        {
                            //checkAndUploadUserImageToStorage(m_firebaseAuth.getCurrentUser().getPhotoUrl(), m_firebaseAuth.getCurrentUser().getEmail());
                            Log.e(TAG, "calling updateUI 5");
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
                        Toast.makeText(SignInActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
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

    @SuppressWarnings("ConstantConditions")
    public void OnForgotPasswordClick(View i_view)
    {
        Log.e(TAG, "OnForgotPasswordClick() >> ");

        String emailStr = m_userEmailEditText.getText().toString();

        if(emailStr.isEmpty())
        {
            Toast.makeText(SignInActivity.this, "Please type an email address in order to reset your password.", Toast.LENGTH_LONG).show();
        }

        else
        {
            m_firebaseAuth.sendPasswordResetEmail(emailStr)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> i_completedTask)
                        {
                            Log.e(TAG, "OnForgotPasswordClick: onComplete() >> " + i_completedTask.isSuccessful());

                            if (i_completedTask.isSuccessful())
                            {
                                showPasswordResetWasSentDialog();
                            } else
                            {
                                Toast.makeText(SignInActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                            Log.e(TAG, "OnForgotPasswordClick: onComplete() << " + i_completedTask.isSuccessful());
                        }
                    });
        }

        Log.e(TAG, "OnForgotPasswordClick() << ");
    }

    public void onSignInAnonymouslyClick(View i_view)
    {
        Log.e(TAG, "onSignInAnonymouslyClick() >>");

        GifPlayer.setAnonymousSignIn(true);
        GifPlayer.playGif();

        long cacheExpiration = 0;

        m_FirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            m_FirebaseRemoteConfig.activateFetched();

                            if (m_FirebaseRemoteConfig.getBoolean("allow_anonymous_user"))
                            {
                                signInAnonymously();
                            }

                            else
                            {
                                Toast.makeText(SignInActivity.this, "Anonymous sign in is not allowed right now.", Toast.LENGTH_LONG).show();
                                GifPlayer.stopGif();
                            }
                        }

                        else
                        {
                            Log.e(TAG, "Fetch Failed", task.getException());
                            Toast.makeText(SignInActivity.this, "Fetch Failed",
                                    Toast.LENGTH_LONG).show();
                            GifPlayer.stopGif();
                        }
                    }
                });

        Log.e(TAG, "onSignInAnonymouslyClick() <<");

    }

    public void onSignUpClick(View i_view)
    {
        Log.e(TAG, "onSignUpClick() >>");

        Intent regIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        regIntent.putExtra("Email", m_userEmailEditText.getText().toString());
        regIntent.putExtra("MAIN_CALL", true);
        startActivity(regIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();

        Log.e(TAG, "onSignUpClick() <<");

    }

    private void findViews()
    {
        Log.e(TAG, "findViews() >>");

        GifPlayer.s_LoadingBar =findViewById(R.id.load_bar);
        m_userEmailEditText = findViewById(R.id.editTextEmail);
        m_userPasswordEditText = findViewById(R.id.editTextPassword);
        m_googleSignInButton = findViewById(R.id.google_sign_in_button);
        m_facebookLoginButton = findViewById(R.id.buttonFacebook);

        Log.e(TAG, "findViews() <<");

    }

    private void googleSignInInit()
    {
        Log.e(TAG, "googleSignInInit() >>");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        m_googleSignInClient = GoogleSignIn.getClient(this, gso);

        m_googleSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View i_view)
            {
                GifPlayer.setGoogleSignIn(true);
                GifPlayer.playGif();
                onClickGoogleButton();
            }
        });

        Log.e(TAG, "googleSignInInit() <<");
    }

    private void onClickGoogleButton()
    {
        Log.e(TAG, "onClickGoogleButton() >>");

        Intent signInIntent = m_googleSignInClient.getSignInIntent();
        GifPlayer.playGif();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);

        Log.e(TAG, "onClickGoogleButton() <<");

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

    @SuppressWarnings("ConstantConditions")
    private void firebaseAuthWithGoogle(final GoogleSignInAccount i_googleSignInAccount)
    {
        Log.e(TAG, "firebaseAuthWithGoogle() >>, id = " + i_googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(i_googleSignInAccount.getIdToken(), null);

        m_firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> i_completedTask)
            {
                if (i_completedTask.isSuccessful())
                {
                    Log.e(TAG, "calling updateUI 2 " + m_firebaseAuth.getCurrentUser().getEmail());

                    Toast.makeText(SignInActivity.this, "Google sign in success!", Toast.LENGTH_SHORT).show();
                    handleAllSignInSuccess("Google");
                }

                else
                {
                    Toast.makeText(SignInActivity.this, "Google sign in error :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.e(TAG, "firebaseAuthWithGoogle() <<");
    }

    private void updateUIAndMoveToCinemaMainActivity()
    {
        Log.e(TAG, "updateUIAndMoveToCinemaMainActivity() >>");

        if(m_firebaseUser != null)
        {
            Intent CinemaMainIntent = new Intent(getApplicationContext(), CinemaMainActivity.class);
            GifPlayer.stopGif();
            startActivity(CinemaMainIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }

        Log.e(TAG, "updateUIAndMoveToCinemaMainActivity() <<");

    }

    private void checkAndUploadUserImageToStorage(final Uri i_internalPhotoUri, String i_email)
    {
        Log.e(TAG,"checkAndUploadUserImageToStorage >>");

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReferenceProfilePic = firebaseStorage.getReference();
        final StorageReference storageImageRef = storageReferenceProfilePic.child("Users Profile Picture/"+ i_email + ".jpg");

        storageImageRef.getDownloadUrl()
                .addOnFailureListener(new OnFailureListener()
                {
                    //Image is not in storage, uploading it.
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e(TAG,"==> Image search failed, uploaded a new image");
                        uploadImageToStorage(storageImageRef, i_internalPhotoUri);
                    }
                });

        Log.e(TAG,"checkAndUploadUserImageToStorage <<");
    }

    @SuppressWarnings("ConstantConditions")
    private void uploadImageToStorage(StorageReference i_storageImageRef, Uri i_internalPhotoUri)
    {
        Log.e(TAG,"uploadImageToStorage >>");

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        i_storageImageRef.putFile(i_internalPhotoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        String storagePhotoURL = taskSnapshot.getDownloadUrl().toString();
                        m_userDetails.setUserPictureUrl(storagePhotoURL);
                        userRef.child(m_firebaseUser.getUid()).child("userPictureUrl").setValue(storagePhotoURL);
                        Log.e(TAG,"Upload image success");
                        Toast.makeText(SignInActivity.this, "Upload User Image Succsses",Toast.LENGTH_LONG).show();
                    }
                });

        Log.e(TAG,"uploadImageToStorage <<");
    }

    private void handleAllSignInSuccess(String i_loginMethod)
    {
        Log.e(TAG,"handleAllSignInSuccess >>");

        m_loginMethod = i_loginMethod;
        m_firebaseUser = m_firebaseAuth.getCurrentUser();
        ifNewUserAddToDBAndUpdateUI();

        Log.e(TAG,"handleAllSignInSuccess <<");

    }

    @SuppressWarnings("ConstantConditions")
    private void overrideUserDetailsInformation(String i_loginMethod)
    {
        Log.e(TAG,"overrideUserDetailsInformation >>");

        switch (i_loginMethod)
        {
            case "Google":
                changeUserDetailsPictureUrlForGoogle(m_userDetails);
                m_userDetails.setUserEmail(m_googleSignInAccount.getEmail());
                break;

            case "Facebook":
                changeUserDetailsPictureUrlForFacebook(m_userDetails);
                setUserEmailToFacebookUser(m_userDetails, m_firebaseUser);
                break;

            case "EmailPassword":
                checkAndUploadUserImageToStorage(m_firebaseAuth.getCurrentUser().getPhotoUrl(), m_firebaseAuth.getCurrentUser().getEmail());
                break;
        }

        Log.e(TAG,"overrideUserDetailsInformation <<");

    }

    @SuppressWarnings("ConstantConditions")
    private void ifNewUserAddToDBAndUpdateUI()
    {
        Log.e(TAG,"ifNewUserAddToDBAndUpdateUI >>");

        m_userDetails = new UserDetails(m_firebaseUser);
        overrideUserDetailsInformation(m_loginMethod);

        if(m_firebaseUser != null)
        {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot snapshot)
                        {
                            if (!snapshot.exists()) //user not exists in DB
                            {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                                userRef.child(m_firebaseUser.getUid()).setValue(m_userDetails);
                            }
                            updateUIAndMoveToCinemaMainActivity();
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) { }
                     });

        }

        Log.e(TAG,"ifNewUserAddToDBAndUpdateUI <<");
    }

    private void facebookLoginInit()
    {
        Log.e(TAG, "facebookLoginInit() >>");

        m_callbackManager = CallbackManager.Factory.create();

        m_facebookLoginButton.setReadPermissions("email", "public_profile");
        m_facebookLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View i_view)
            {
                GifPlayer.setFacebookSignIn(true);
                GifPlayer.playGif();
            }
        });

        m_facebookLoginButton.registerCallback(m_callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult i_loginResult)
            {
                Toast.makeText(SignInActivity.this, "Facebook sign in success!", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(i_loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                GifPlayer.stopGif();
                Toast.makeText(SignInActivity.this, "Facebook sign in canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException i_facebookException)
            {
                Toast.makeText(SignInActivity.this, "Facebook sign in error :(", Toast.LENGTH_SHORT).show();
            }
        });

        new AccessTokenTracker()
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

    @SuppressWarnings("ConstantConditions")
    private void handleFacebookAccessToken(AccessToken i_accessToken)
    {
        Log.e(TAG, "handleFacebookAccessToken () >>" + i_accessToken.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(i_accessToken.getToken());

        m_firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> i_completedTask)
                {
                Log.e(TAG, "Facebook: onComplete() >> " + i_completedTask.isSuccessful());

                if (i_completedTask.isSuccessful())
                {
                    handleAllSignInSuccess("Facebook");
                }

                else
                {
                    GifPlayer.stopGif();
                    Log.e(TAG, "unsuccessful sign in to google");
                    Toast.makeText(SignInActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                Log.e(TAG, "Facebook: onComplete() <<");
            }
        });

        Log.e(TAG, "handleFacebookAccessToken () <<");
    }

    private void firebaseAuthenticationInit()
    {
        Log.e(TAG, "firebaseAuthenticationInit() >>");

        //Obtain reference to the current authentication
        m_firebaseAuth = FirebaseAuth.getInstance();

        m_AuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth i_firebaseAuth)
            {
            }
        };

        Log.e(TAG, "firebaseAuthenticationInit() <<");
    }

    private void showWaitingForEmailVerificationDialog()
    {
        Log.e(TAG,"showWaitingForEmailVerificationDialog >>");

        new AlertDialog.Builder(this)
                .setMessage("Waiting for email verification.\nPlease verify your account and sign in again.\n")
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();

        Log.e(TAG,"showWaitingForEmailVerificationDialog <<");
    }

    private void showPasswordResetWasSentDialog()
    {
        Log.e(TAG,"showPasswordResetWasSentDialog >>");

        new AlertDialog.Builder(this)
                .setMessage("A password reset request has been sent to:\n" + m_userEmailEditText.getText().toString())
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();

        Log.e(TAG,"showPasswordResetWasSentDialog <<");
    }

    private void signInAnonymously()
    {
        Log.e(TAG,"signInAnonymously >>");

        m_firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInAnonymously: success");
                            updateProfile();
                        }

                        else
                        {
                            Log.w(TAG, "signInAnonymously: failure", task.getException());
                        }
                    }
                });

        Log.e(TAG,"signInAnonymously <<");
    }

    @SuppressWarnings("ConstantConditions")
    private void updateProfile()
    {
        Log.e(TAG,"updateProfile >>");

        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName("Anonymous")
                .build();

        m_firebaseAuth.getCurrentUser().updateProfile(updateProfile)
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> i_completedTask)
                    {
                        if(i_completedTask.isSuccessful())
                        {
                            handleAllSignInSuccess("Anonymously");
                        }
                    }
                });

        Log.e(TAG,"updateProfile <<");

    }
}