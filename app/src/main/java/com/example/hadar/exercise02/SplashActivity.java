package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity
{
    private UserDetails m_userDetails;
    private GoogleSignInAccount m_googleSignInAccount;
    private FirebaseUser m_firebaseUser;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_splash);

        m_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(3000);
                    moveToNextActivity();
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }

    public void moveToNextActivity()
    {
        Intent nextActivityIntent;

        if (m_googleSignInAccount != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            setUserDetailsFromGoogleAccount();
            nextActivityIntent.putExtra("User Details", m_userDetails);
        }

        else if(m_firebaseUser != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            setUserDetailsFromFirebaseAccount();
            nextActivityIntent.putExtra("User Details", m_userDetails);
        }
        else
            nextActivityIntent = new Intent(getApplicationContext(), MainActivity.class);


        startActivity(nextActivityIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        m_googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    public void setUserDetailsFromGoogleAccount()
    {
        m_userDetails = new UserDetails(m_googleSignInAccount);
        changeUserDetailsPictureUrl(MainActivity.GOOGLE_URL_PATH_TO_REMOVE, MainActivity.GOOGLE_URL_PATH_TO_ADD);
    }

    public void setUserDetailsFromFirebaseAccount()
    {
        m_userDetails = new UserDetails(m_firebaseUser);
        MainActivity.changeUserDetailsPictureUrlForFacebook(m_userDetails);
        MainActivity.setUserEmailToFacebookUser(m_userDetails, m_firebaseUser);
    }

    public void changeUserDetailsPictureUrl(String i_originalPieceOfUrlToRemove, String i_newPieceOfUrlToAdd)
    {
        String userDetailsPhotoUrl = m_userDetails.getUserPictureUrl();

        if(userDetailsPhotoUrl != null)
        {
            String newPhotoPath = userDetailsPhotoUrl.replace(i_originalPieceOfUrlToRemove, i_newPieceOfUrlToAdd);
            m_userDetails.setUserPictureUrl(newPhotoPath);
        }
    }
}