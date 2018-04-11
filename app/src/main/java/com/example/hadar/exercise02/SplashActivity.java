package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SplashActivity extends AppCompatActivity
{
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
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
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

    @Override
    public void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account)
    {
        Intent nextActivityIntent;

        if(account!=null) {
            nextActivityIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);

        }
        else {
            nextActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        }

        startActivity(nextActivityIntent);

    }
}
