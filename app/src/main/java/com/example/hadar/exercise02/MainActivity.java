package com.example.hadar.exercise02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity
{
    private static int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth m_firebaseAuth;
    private GoogleSignInClient m_googleSignInClient;
    private UserDetails m_userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_firebaseAuth = FirebaseAuth.getInstance();

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

    public void onClickGoogleButton()
    {
        Intent signInIntent = m_googleSignInClient.getSignInIntent();
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
        }
    }
}
