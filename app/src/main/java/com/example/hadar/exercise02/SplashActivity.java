package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SplashActivity extends AppCompatActivity
{
    private UserDetails m_userDetails;
    private GoogleSignInAccount m_googleSignInAccount;

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
        if (m_googleSignInAccount != null)
        {
            nextActivityIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            setUserDetailsFromGoogleAccount();
            nextActivityIntent.putExtra("User Details", m_userDetails);
        }
        else
            nextActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(nextActivityIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        m_googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    public void setUserDetailsFromGoogleAccount()
    {
        m_userDetails = new UserDetails(m_googleSignInAccount);
        changeUserDetailsPictureUrl(MainActivity.GOOGLE_URL_PATH_TO_REMOVE, MainActivity.GOOGLE_URL_PATH_TO_ADD);
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