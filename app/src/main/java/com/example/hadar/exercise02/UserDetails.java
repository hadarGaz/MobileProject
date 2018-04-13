package com.example.hadar.exercise02;

import android.net.Uri;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class UserDetails implements Serializable
{
    private String m_userName;
    private String m_userEmail;
    private String m_userPictureUrl;

    public UserDetails(String i_UserName, String i_UserEmail, String i_UserPictureUrl)
    {
        m_userName = i_UserName;
        m_userEmail = i_UserEmail;
        m_userPictureUrl = i_UserPictureUrl;
    }

    public UserDetails(FirebaseUser i_firebaseUser)
    {
        String userName = i_firebaseUser.getDisplayName();
        String userEmail = i_firebaseUser.getEmail();
        String userPictureUrl = i_firebaseUser.getPhotoUrl().toString();

        m_userName = userName;
        m_userEmail = userEmail;
        m_userPictureUrl = userPictureUrl;
    }

    public UserDetails(GoogleSignInAccount i_googleSignInAccount)
    {
        String userName = i_googleSignInAccount.getDisplayName();
        String userEmail = i_googleSignInAccount.getEmail();
        String userPictureUrl = i_googleSignInAccount.getPhotoUrl().toString();

        m_userName = userName;
        m_userEmail = userEmail;
        m_userPictureUrl = userPictureUrl;
    }

    public String getUserName()
    {
        return m_userName;
    }

    public String getUserEmail()
    {
        return m_userEmail;
    }

    public String getUserPictureUrl()
    {
        return m_userPictureUrl;
    }

    public void setUserName(String i_UserNameToSet)
    {
        m_userName = i_UserNameToSet;
    }

    public void setUserEmail(String i_UserEmailToSet)
    {
        m_userEmail = i_UserEmailToSet;
    }

    public void setUserPictureUrl(String i_UserPictureUrlToSet)
    {
        m_userPictureUrl = i_UserPictureUrlToSet;
    }
}