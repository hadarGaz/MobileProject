package com.project.hadar.AcadeMovie.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.project.hadar.AcadeMovie.R;

public class AskForSignInActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_sign_in);
    }

    public void onClickSignInButton(View i_view)
    {
        Intent goToSignInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        goToSignInActivity.putExtra("Source Activity", "AskForSignInActivity");
        startActivity(goToSignInActivity);
    }

    public void onClickBackToAppButton(View i_view)
    {
        super.onBackPressed();
        finish();
    }

    public void onClickSignMeUp(View i_view)
    {
        Intent goToRegistrationActivity = new Intent(getApplicationContext(), RegistrationActivity.class);
        goToRegistrationActivity.putExtra("Source Activity", "AskForSignInActivity");
        startActivity(goToRegistrationActivity);
    }
}
