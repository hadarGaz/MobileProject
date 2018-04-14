package com.example.hadar.exercise02;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity
{
    public static final String TAG = "RegistrationActivity";
    private static final int RESULT_LOAD_IMAGE = 1;
    private FirebaseAuth m_firebaseAuth;
    boolean m_isImageUploaded = false;
    EditText m_userEmailEditText, m_userNameEditText, m_userPasswordEditText;
    ImageView m_userPictureImageView;
    Uri m_userPictureUri;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViews();
        checkIfEmailHasAlreadyBeenWritten();
    }

    private void findViews()
    {
        m_firebaseAuth = FirebaseAuth.getInstance();
        m_userEmailEditText = findViewById(R.id.editTextEmail);
        m_userPasswordEditText = findViewById(R.id.editTextPassword);
        m_userNameEditText = findViewById(R.id.editTextPersonName);
        m_userPictureImageView = findViewById(R.id.imageViewSelectImage);
    }
    private void checkIfEmailHasAlreadyBeenWritten()
    {
        String emailStr;
        if( (emailStr = (String) getIntent().getSerializableExtra("Email")) != null)
        {
            m_userEmailEditText.setText(emailStr);
        }
    }

    public void onSubmit(View i_view)
    {
        //check validation
        if(detailsValidation())
        {
            Task<AuthResult> authResult;
            authResult = m_firebaseAuth.createUserWithEmailAndPassword(m_userEmailEditText.getText().toString(), m_userPasswordEditText.getText().toString());

            authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.isSuccessful());

                    if (task.isSuccessful() == false)
                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    else
                    {
                        updateNameAndUriToUserAndUpdateUI();
                    }

                    Log.e(TAG, "Email/Pass Auth: onComplete() <<");
                }
            });
        }
    }

    private void updateNameAndUriToUserAndUpdateUI()
    {
        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
            .setDisplayName(m_userNameEditText.getText().toString())
            .setPhotoUri(m_userPictureUri).build();

        m_firebaseAuth.getCurrentUser().updateProfile(updateProfile)
            .addOnCompleteListener(this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    sendVerification();
                }
        });
    }

    private void sendVerification()
    {
        m_firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this,
                                    "Verification email sent to " + m_firebaseAuth.getCurrentUser().getEmail(),
                                    Toast.LENGTH_LONG).show();
                            m_firebaseAuth.signOut();
                            goBackToMainActivity();
                        }
                    }
                });
    }

    public void goBackToMainActivity()
    {
        Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backToMainIntent);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onSelectImageClick(View i_view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int i_requestCode, int i_resultCode, Intent i_dataIntent)
    {
        super.onActivityResult(i_requestCode, i_resultCode, i_dataIntent);

        if (i_requestCode == RESULT_LOAD_IMAGE && i_resultCode == RESULT_OK && i_dataIntent != null)
        {
            m_userPictureUri = i_dataIntent.getData();
            m_userPictureImageView.setImageURI(m_userPictureUri);

            m_isImageUploaded = true;
        }
    }

    private boolean detailsValidation()
    {
        boolean resReturn = true;
        if (!verifyName(m_userNameEditText.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Invalid Name", Toast.LENGTH_SHORT).show();
            resReturn = false;
        }
        else if (!verifyEmail(m_userEmailEditText.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            resReturn = false;
        }
        else if (!verifyPassword(m_userPasswordEditText.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "\n" +
                    "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
            resReturn = false;
        }
        else if(!m_isImageUploaded) {
            Toast.makeText(RegistrationActivity.this, "Must select an image", Toast.LENGTH_SHORT).show();
            resReturn = false;
        }
        return resReturn;
    }

    public boolean verifyName(String i_fullName)
    {
        String RegEx = "^[a-zA-Z\\s]*$";

        if (i_fullName.matches(""))
            return false;

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_fullName);

        return matcher.matches();
    }

    //Verifying an Email:
    public boolean verifyEmail(String i_email)
    {
        String RegEx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (i_email.matches(""))
            return false;

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_email);

        return matcher.matches();
    }

    public boolean verifyPassword(String i_Password)
    {
        if(i_Password.length() < 6 || i_Password == null || i_Password.isEmpty())
            return false;

        return true;
    }
}
