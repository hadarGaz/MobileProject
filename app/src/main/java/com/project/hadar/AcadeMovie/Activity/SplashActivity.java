package com.project.hadar.AcadeMovie.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.hadar.AcadeMovie.Analytics.AnalyticsManager;
import com.project.hadar.AcadeMovie.R;
import com.project.hadar.AcadeMovie.model.UserDetails;
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

        m_googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        m_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AnalyticsManager.getInstance().init(getApplicationContext());

        startRunThread();
    }

    private void startRunThread()
    {
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

    private void moveToNextActivity()
    {
        Intent nextActivityIntent;

        if (m_googleSignInAccount != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), CinemaMainActivity.class);
            createUserDetailsFromGoogleAccount();
            nextActivityIntent.putExtra("User Details", m_userDetails);
        }

        else if(m_firebaseUser != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), CinemaMainActivity.class);
            createUserDetailsFromFirebaseAccount();
            nextActivityIntent.putExtra("User Details", m_userDetails);
        }
        else
            nextActivityIntent = new Intent(getApplicationContext(), SignInActivity.class);


        startActivity(nextActivityIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void createUserDetailsFromGoogleAccount()
    {
        m_userDetails = new UserDetails(m_googleSignInAccount);
        SignInActivity.changeUserDetailsPictureUrlForGoogle(m_userDetails);
    }

    private void createUserDetailsFromFirebaseAccount()
    {
        m_userDetails = new UserDetails(m_firebaseUser);
        SignInActivity.changeUserDetailsPictureUrlForFacebook(m_userDetails);
        SignInActivity.setUserEmailToFacebookUser(m_userDetails, m_firebaseUser);
    }
}