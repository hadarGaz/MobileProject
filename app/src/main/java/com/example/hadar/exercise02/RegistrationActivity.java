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
    private boolean m_isImageUploaded = false;
    private EditText m_Email, m_Name, m_Password;
    private ImageView m_ImageView;
    private Uri m_userPictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void findViews()
    {
        m_firebaseAuth = FirebaseAuth.getInstance();
        m_Email = findViewById(R.id.editTextEmail);
        m_Password = findViewById(R.id.editTextPassword);
        m_Name = findViewById(R.id.editTextPersonName);
        m_ImageView = findViewById(R.id.imageViewSelectImage);
    }
    private void checkIfEmailHasAlreadyBeenWritten()
    {
        String emailStr;
        if( (emailStr = (String) getIntent().getSerializableExtra("Email")) != null)
        {
            m_Email.setText(emailStr);
        }
    }

    public void onSubmit(View v)
    {
        //check validation
        if(detailsValidation())
        {
            Task<AuthResult> authResult = m_firebaseAuth.createUserWithEmailAndPassword(m_Email.getText().toString(), m_Password.getText().toString());

            authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.isSuccessful());

                    if (!task.isSuccessful())
                    {
                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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

    private void updateNameAndUriToUserAndSendVerification()
    {
        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
            .setDisplayName(m_Name.getText().toString())
            .setPhotoUri(m_userPictureUri).build();

        m_firebaseAuth.getCurrentUser().updateProfile(updateProfile)
            .addOnCompleteListener(this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    sendVerificationAndGoBackToMainActivity();
                }
            });
    }
    private void sendVerificationAndGoBackToMainActivity()
    {
        m_firebaseAuth.getCurrentUser().sendEmailVerification()
        .addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> i_task)
            {
                if (i_task.isSuccessful())
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
            m_ImageView.setImageURI(m_userPictureUri);

            m_isImageUploaded = true;
        }
    }

    private boolean detailsValidation()
    {
        try
        {
            verifyName(m_Name.getText().toString());
            verifyEmail(m_Email.getText().toString());
            verifyPassword(m_Password.getText().toString());

             if(!m_isImageUploaded)
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

    private void verifyName(String i_fullName)throws Exception
    {
        String RegEx = "^[a-zA-Z\\s]*$";

        if (i_fullName.matches(""))
            throw new Exception("Name field is empty");

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_fullName);

        if(matcher.matches() == false)
            throw new Exception("Invalid Name, only character");

    }

    private void verifyEmail(String i_email)throws Exception
    {
        String RegEx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (i_email.matches(""))
            throw new Exception("Email field is empty");

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_email);

        if(matcher.matches() == false)
            throw new Exception("Invalid Email, doesn't match to email format ");
    }

    private void verifyPassword(String i_password)throws Exception
    {
        if(i_password.length() < 6 )
            throw new Exception("Password must contain at least 6 characters");

        else if(i_password == null || i_password.isEmpty())
            throw new Exception("Password field is empty");
    }
}
