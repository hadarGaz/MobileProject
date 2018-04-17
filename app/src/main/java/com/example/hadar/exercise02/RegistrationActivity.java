package com.example.hadar.exercise02;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
    private boolean m_isPictureUploaded = false;
    private EditText m_emailEditText;
    private EditText m_userNameEditText;
    private EditText m_passwordEditText;
    private ImageView m_uploadedPictureImageView;
    private Uri m_userPictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViews();
        checkIfEmailHasAlreadyBeenWritten();
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leaving Registration Area")
                .setMessage("Are you sure you want to leave the registration?\nAll information will get lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface i_dialogInterface, int i_num)
                    {
                        goBackToMainActivity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void findViews()
    {
        m_firebaseAuth = FirebaseAuth.getInstance();
        m_emailEditText = findViewById(R.id.editTextEmail);
        m_passwordEditText = findViewById(R.id.editTextPassword);
        m_userNameEditText = findViewById(R.id.editTextPersonName);
        m_uploadedPictureImageView = findViewById(R.id.imageViewSelectImage);
    }

    public void checkIfEmailHasAlreadyBeenWritten()
    {
        String emailStr;
        if( (emailStr = (String) getIntent().getSerializableExtra("Email")) != null)
        {
            m_emailEditText.setText(emailStr);
        }
    }

    public void onSubmit(View v)
    {
        //check validation
        if(detailsValidation())
        {
            Task<AuthResult> authResult = m_firebaseAuth.createUserWithEmailAndPassword(m_emailEditText.getText().toString(), m_passwordEditText.getText().toString());

            authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> i_completedTask)
                {
                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + i_completedTask.isSuccessful());

                    if (!i_completedTask.isSuccessful())
                    {
                        Toast.makeText(RegistrationActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        updateNameAndUriToUserAndSendVerification();
                    }

                    Log.e(TAG, "Email/Pass Auth: onComplete() <<");
                }
            });
        }
    }

    public void updateNameAndUriToUserAndSendVerification()
    {
        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
            .setDisplayName(m_userNameEditText.getText().toString())
            .setPhotoUri(m_userPictureUri).build();

        m_firebaseAuth.getCurrentUser().updateProfile(updateProfile)
            .addOnCompleteListener(this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> i_completedTask)
                {
                    sendVerificationAndGoBackToMainActivity();
                }
            });
    }

    public void sendVerificationAndGoBackToMainActivity()
    {
        m_firebaseAuth.getCurrentUser().sendEmailVerification()
            .addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> i_completedTask)
                {
                    if (i_completedTask.isSuccessful())
                    {
                        showEmailSentDialogAndGoBackToMail();
                    }
                }
            });
    }

    public void showEmailSentDialogAndGoBackToMail()
    {
        new AlertDialog.Builder(this)
                .setMessage("Verification email sent to:\n" + m_firebaseAuth.getCurrentUser().getEmail())
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        m_firebaseAuth.signOut();
                        goBackToMainActivity();
                    }
                })
                .show();
    }

    public void goBackToMainActivity()
    {
        Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backToMainIntent);
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
            m_uploadedPictureImageView.setImageURI(m_userPictureUri);

            m_isPictureUploaded = true;
        }
    }

    public boolean detailsValidation()
    {
        try
        {
            verifyName(m_userNameEditText.getText().toString());
            verifyEmail(m_emailEditText.getText().toString());
            verifyPassword(m_passwordEditText.getText().toString());

             if(m_isPictureUploaded == false)
             {
                 throw new Exception("Please choose an image");
             }
        }

        catch (Exception e)
        {
            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void verifyName(String i_fullName)throws Exception
    {
        String RegEx = "^[a-zA-Z\\s]*$";

        if (i_fullName.matches(""))
        {
            throw new Exception("Name field is empty");
        }

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_fullName);

        if(matcher.matches() == false)
        {
            throw new Exception("Invalid Name, only character");
        }

    }

    public void verifyEmail(String i_email)throws Exception
    {
        String RegEx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (i_email.matches(""))
        {
            throw new Exception("Email field is empty");
        }

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_email);

        if(matcher.matches() == false)
        {
            throw new Exception("Invalid Email, doesn't match to email format ");
        }
    }

    public void verifyPassword(String i_password)throws Exception
    {
        if(i_password.length() < 6 )
        {
            throw new Exception("Password must contain at least 6 characters");
        }

        else if(i_password == null || i_password.isEmpty())
        {
            throw new Exception("Password field is empty");
        }
    }
}
