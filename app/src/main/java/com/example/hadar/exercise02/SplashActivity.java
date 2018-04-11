package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SplashActivity extends AppCompatActivity
{
    GoogleSignInAccount m_googleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread() {
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

        //Add || m_facebookAccount != null || m_emailPasswordAccount != null ...
        if (m_googleAccount != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            nextActivityIntent.putExtra("Google Account", m_googleAccount);
        }
        else
            nextActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(nextActivityIntent);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        m_googleAccount = GoogleSignIn.getLastSignedInAccount(this);
    }
}