package com.example.hadar.AcadeMovie.Activity;

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
import com.example.hadar.AcadeMovie.R;
import com.example.hadar.AcadeMovie.model.DetailsValidation;
import com.example.hadar.AcadeMovie.model.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity
{
    private static final String TAG = "RegistrationActivity";
    private String m_sourceActivity;
    private static final int RESULT_LOAD_IMAGE = 1;
    private FirebaseAuth m_firebaseAuth;
    private boolean m_isPictureUploaded = false;
    private EditText m_emailEditText;
    private EditText m_userNameEditText;
    private EditText m_passwordEditText;
    private ImageView m_uploadedPictureImageView;
    private Uri m_userPictureUri;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_registration);

        m_sourceActivity = getIntent().getStringExtra("Source Activity");
        findViews();
        checkIfEmailHasAlreadyBeenWritten();
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
                        if(("AskForSignInActivity").equals(m_sourceActivity))
                        {
                            goBackToAskForSignInActivity();
                        }

                        else
                        {
                            goBackToMainActivity();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    public void onSelectImageClick(View i_view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @SuppressWarnings("ConstantConditions")
    public void onSubmit(View i_view)
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

                    if (i_completedTask.isSuccessful())
                    {
                        updateNameAndUriToUserAndSendVerification();
                    }

                    else
                    {
                        Toast.makeText(RegistrationActivity.this, i_completedTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    Log.e(TAG, "Email/Pass Auth: onComplete() <<");
                }
            });
        }
    }

    private void findViews()
    {
        m_firebaseAuth = FirebaseAuth.getInstance();
        m_emailEditText = findViewById(R.id.editTextEmail);
        m_passwordEditText = findViewById(R.id.editTextPassword);
        m_userNameEditText = findViewById(R.id.editTextPersonName);
        m_uploadedPictureImageView = findViewById(R.id.imageViewSelectImage);
    }

    private void checkIfEmailHasAlreadyBeenWritten()
    {
        String emailStr;
        if( (emailStr = (String) getIntent().getSerializableExtra("Email")) != null)
        {
            m_emailEditText.setText(emailStr);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void updateNameAndUriToUserAndSendVerification()
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
                    addUserToDB();
                    sendVerificationAndGoBackToMainActivity();
                }
            });
    }

    @SuppressWarnings("ConstantConditions")
    private void addUserToDB()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(m_firebaseAuth.getCurrentUser().getUid()).setValue(new UserDetails(m_firebaseAuth.getCurrentUser()));
    }

    @SuppressWarnings("ConstantConditions")
    private void sendVerificationAndGoBackToMainActivity()
    {
        m_firebaseAuth.getCurrentUser().sendEmailVerification()
            .addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> i_completedTask)
                {
                    if (i_completedTask.isSuccessful())
                    {
                        showEmailSentDialogAndGoBackToMainActivity();
                    }
                }
            });
    }

    @SuppressWarnings("ConstantConditions")
    private void showEmailSentDialogAndGoBackToMainActivity()
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

    private void goBackToAskForSignInActivity()
    {
        super.onBackPressed();
        finish();
    }

    private void goBackToMainActivity()
    {
        Intent backToMainIntent = new Intent(getApplicationContext(), SignInActivity.class);
        backToMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backToMainIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private boolean detailsValidation()
    {
        DetailsValidation detailsValidation = new DetailsValidation();

        try
        {
            detailsValidation.verifyName(m_userNameEditText.getText().toString());
            detailsValidation.verifyEmail(m_emailEditText.getText().toString());
            detailsValidation.verifyPassword(m_passwordEditText.getText().toString());

             if(!m_isPictureUploaded)
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
}