package com.example.hadar.exercise02;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import java.io.FileNotFoundException;

public class UserDetailsActivity extends AppCompatActivity
{
    private ImageView m_userPictureImageView;
    private String m_userPictureUrl;
    private ImageButton m_signOutButton;
    private TextView m_TextViewUserName, m_TextViewUserEmail;
    private Intent m_inputIntent;
    private UserDetails m_userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        findViews();
        getIntentInput();
        displayUserDetails();

        m_signOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View i_view)
            {
                signOutAllAccounts();
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

    public void displayUserDetails()
    {
        showUserPicture();
        showUserName();
        showUserEmail();
    }

    public void showUserPicture()
    {
        m_userPictureUrl = m_userDetails.getUserPictureUrl();

        //MUST TEST ON NON-PICTURE PROFILES!
        if (m_userPictureUrl != "null")
        {
          Glide.with(this).load(m_userPictureUrl).into(m_userPictureImageView);
        }
    }

    public void showUserName()
    {
        m_TextViewUserName.setText("User Name: "+m_userDetails.getUserName());
    }

    public void showUserEmail()
    {
        m_TextViewUserEmail.setText("Email: "+m_userDetails.getUserEmail());
    }

    private void getIntentInput()
    {
        m_inputIntent = getIntent();
        m_userDetails = (UserDetails) m_inputIntent.getSerializableExtra("User Details");
    }

    public void findViews()
    {
        m_TextViewUserName = findViewById(R.id.user_name);
        m_TextViewUserEmail = findViewById(R.id.user_email);
        m_signOutButton = findViewById(R.id.sign_out_button);
        m_userPictureImageView = findViewById(R.id.user_picture);
    }

    public void signOutAllAccounts()
    {
        //Add signOutFacebookAccount, signOutEmailPassAccount
        signOutGoogleAccount();
    }

    public void signOutGoogleAccount()
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

    public void goBackToMainActivity()
    {
        Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backToMainIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
