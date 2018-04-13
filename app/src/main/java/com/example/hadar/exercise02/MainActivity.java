package com.example.hadar.exercise02;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import pl.droidsonroids.gif.GifTextView;

public class MainActivity extends Activity
{
    private static int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth m_firebaseAuth;
    private GoogleSignInClient m_googleSignInClient;
    private UserDetails m_userDetails;
    private GifTextView m_googleLoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_firebaseAuth = FirebaseAuth.getInstance();
        m_googleLoadingBar=findViewById(R.id.google_load_bar);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
        playGif();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount googleSignInAccount = completedTask.getResult(ApiException.class);
            updateUI(googleSignInAccount); // Signed in successfully, show authenticated UI.
        }

        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount i_GoogleSignInAccount)
    {
        if(i_GoogleSignInAccount != null)
        {
            Intent userDetails = new Intent(getApplicationContext(), UserDetailsActivity.class);
            m_userDetails = new UserDetails(i_GoogleSignInAccount);
            userDetails.putExtra("User Details", m_userDetails);
            startActivity(userDetails);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


    public void playGif(){
        final Animation googleLoader = new AlphaAnimation(1.f, 1.f);
        //googleLoader.setDuration(20000);
        googleLoader.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation)
            {

                m_googleLoadingBar.setBackgroundResource(R.drawable.google_dark_load);
            }

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                m_googleLoadingBar.clearAnimation();
            }
        });
        m_googleLoadingBar.startAnimation(googleLoader);
    }

}
