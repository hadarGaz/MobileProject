package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class UserDetailsActivity extends AppCompatActivity
{
    String m_userName, m_userEmail;
    ImageView m_userPhoto;
    Button m_signOutButton;
    TextView m_TextViewUserName, m_TextViewUserEmail;
    Intent m_inputIntent;
    GoogleSignInAccount m_googleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        findViews();
        getIntentInput();
    }

    private void getIntentInput()
    {
        m_inputIntent = getIntent();
        m_googleAccount = m_inputIntent.getParcelableExtra("Google Account");
    }


    public void findViews()
    {
        m_TextViewUserName = findViewById(R.id.user_name);
        m_TextViewUserEmail = findViewById(R.id.user_email);
        m_signOutButton = findViewById(R.id.sign_out_button);
    }
}
