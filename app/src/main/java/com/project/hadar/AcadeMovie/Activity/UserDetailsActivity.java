package com.project.hadar.AcadeMovie.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.project.hadar.AcadeMovie.Model.GifPlayer;
import com.project.hadar.AcadeMovie.R;
import com.project.hadar.AcadeMovie.Model.UserDetails;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class UserDetailsActivity extends AppCompatActivity
{
    private ImageView m_userPictureImageView;
    private Button m_signOutButton;
    private TextView m_UserNameTextView;
    private TextView m_UserEmailTextView;
    private UserDetails m_userDetails;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_user_details);

        findViews();
        getIntentInput();
        displayUserDetails();

        m_signOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View i_view)
            {
                turnOffAllGifs();
                signOutAllAccounts();
            }
        });
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void turnOffAllGifs()
    {
        GifPlayer.setAnonymousSignIn(false);
        GifPlayer.setFacebookSignIn(false);
        GifPlayer.setGoogleSignIn(false);
    }

    private void displayUserDetails()
    {
        showUserPicture();
        showUserName();
        showUserEmail();
    }

    public void showUserPicture()
    {
        String userPictureUrl = m_userDetails.getUserPictureUrl();

        if (userPictureUrl != null)
        {
            m_userPictureImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(userPictureUrl).into(m_userPictureImageView);
        }
    }

    private void showUserName()
    {
        m_UserNameTextView.setText(m_userDetails.getUserName());
    }

    private void showUserEmail()
    {
        m_UserEmailTextView.setText(m_userDetails.getUserEmail());
    }

    private void getIntentInput()
    {
        Intent inputIntent = getIntent();
        m_userDetails = (UserDetails) inputIntent.getSerializableExtra("User Details");
    }

    private void findViews()
    {
        m_UserNameTextView = findViewById(R.id.TextViewUserName);
        m_UserEmailTextView = findViewById(R.id.TextViewEmail);
        m_signOutButton = findViewById(R.id.buttonSignOut);
        m_userPictureImageView = findViewById(R.id.ImageViewUserPic);
    }

    private void signOutAllAccounts()
    {
        signOutEmailPassAndFacebookAccount();
        signOutGoogleAccount();
    }

    private void signOutEmailPassAndFacebookAccount()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goBackToMainActivity();
    }

    private void signOutGoogleAccount()
    {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(googleSignInAccount != null)
        {
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestProfile()
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut();
            goBackToMainActivity();
        }
    }

    private void goBackToMainActivity()
    {
        Intent backToMainIntent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(backToMainIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}