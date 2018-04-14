package com.example.hadar.exercise02;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity
{
    private UserDetails m_userDetails;
    private GoogleSignInAccount m_googleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(3000);
                    moveToNextActivity();
                    finish();
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

        //Add else if m_facebookAccount != null else if m_emailPasswordAccount != null ...
        if (m_googleAccount != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            setUserDetailsFromGoogleAccount();
            nextActivityIntent.putExtra("User Details", m_userDetails);
        }
        else
            nextActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(nextActivityIntent);
       // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        m_googleAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    public void setUserDetailsFromGoogleAccount()
    {
        m_userDetails = new UserDetails(m_googleAccount);
    }
}