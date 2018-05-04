package com.example.hadar.exercise02.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.example.hadar.exercise02.Activity.SignInActivity;
import com.example.hadar.exercise02.Activity.UserDetailsActivity;
import com.example.hadar.exercise02.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileWidget
{
    private static final String TAG = "ProfileWidget";

    private static void moveToUserDetailsActivity(Context i_activityContext, UserDetails i_userDetails)
    {
        Log.e(TAG, "moveToUserDetailsActivity() >>");

        Intent moveToUserDetailsActivityIntent = new Intent(i_activityContext.getApplicationContext(), UserDetailsActivity.class);
        moveToUserDetailsActivityIntent.putExtra("User Details", i_userDetails);
        i_activityContext.startActivity(moveToUserDetailsActivityIntent);
        //i_activityContext.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        Log.e(TAG, "moveToUserDetailsActivity() <<");
    }

    private static void signOutAllAccountsAndMoveToSignInActivity(Context i_activityContext)
    {
        signOutAllAccount(i_activityContext);
        moveToSignInActivity(i_activityContext);
    }

    private static void signOutAllAccount(Context i_activityContext)
    {
        signOutEmailPassAndFacebookAccount();
        signOutGoogleAccount(i_activityContext);
    }

    private static void signOutEmailPassAndFacebookAccount()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    private static void signOutGoogleAccount(Context i_activityContext)
    {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(i_activityContext);

        if(googleSignInAccount != null)
        {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(i_activityContext.getString(R.string.default_web_client_id))
                    .requestProfile()
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(i_activityContext, googleSignInOptions);
            googleSignInClient.signOut();
        }
    }

    private static void moveToSignInActivity(Context i_activityContext)
    {
        GifPlayer.stopGif();
        Intent moveToSignInActivityIntent = new Intent(i_activityContext.getApplicationContext(), SignInActivity.class);
        i_activityContext.startActivity(moveToSignInActivityIntent);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        ((Activity)i_activityContext).finish();
    }

    public static void onClickProfileWidget(final Context i_activityContext, ImageButton i_activityImageButton, final UserDetails i_userDetails)
    {
        Log.e(TAG, "onClickProfileWidget() >>");

        PopupMenu popup = new PopupMenu(i_activityContext, i_activityImageButton);
        popup.getMenuInflater().inflate(R.menu.activity_user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem i_item)
            {
                if (i_item.getItemId() == R.id.viewProfile)
                {
                    moveToUserDetailsActivity(i_activityContext, i_userDetails);
                }

                else
                {
                    signOutAllAccountsAndMoveToSignInActivity(i_activityContext);
                }

                return true;
            }
        });

        popup.show();

        Log.e(TAG, "onClickProfileWidget() <<");
    }

    public static void displayUserImage(Context i_activityContext, ImageButton i_activityImageButton, UserDetails i_userDetails)
    {
        Log.e(TAG, "displayUserImage() >> ing url= "+ i_userDetails.getUserPictureUrl());

        i_activityImageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(i_activityContext).load(i_userDetails.getUserPictureUrl()).into(i_activityImageButton);
        Log.e(TAG, "displayUserImage() << ");
    }

    public static void pauseGlideRequests()
    {
        Glide.with(getApplicationContext()).pauseRequests();
    }
}
