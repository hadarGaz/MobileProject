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

    public UserDetails(String i_userName, String i_userEmail, String i_userPictureUrl)
    {
        m_userName = i_userName;
        m_userEmail = i_userEmail;
        m_userPictureUrl = i_userPictureUrl;
    }

    public UserDetails(FirebaseUser i_firebaseUser)
    {
        m_userName = i_firebaseUser.getDisplayName();
        m_userEmail = i_firebaseUser.getEmail();
        Uri userPictureUri = i_firebaseUser.getPhotoUrl();

        if(userPictureUri != null)
        {
            m_userPictureUrl = userPictureUri.toString();
        }

        else
        {
            m_userPictureUrl = null;
        }
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

    public void setUserName(String i_userNameToSet)
    {
        m_userName = i_userNameToSet;
    }

    public void setUserEmail(String i_userEmailToSet)
    {
        m_userEmail = i_userEmailToSet;
    }

    public void setUserPictureUrl(String i_userPictureUrlToSet)
    {
        m_userPictureUrl = i_userPictureUrlToSet;
    }

    public String toString()
    {
        return "User Name: " + m_userName +", Email: " + m_userEmail;
    }
}