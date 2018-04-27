package com.example.hadar.exercise02.model;


public class Review {
    private String m_textReview;
    private byte m_rating;
    private String m_userEmail;


    public Review() {}


    public Review(String i_textReview, byte i_rating, String i_userName)
    {
        this.m_textReview = i_textReview;
        this.m_rating = i_rating;
        this.m_userEmail = i_userName;
    }


    public String getTextReview()
    {
        return m_textReview;
    }

    public byte getRating()
    {
        return m_rating;
    }

    public String getUserEmail()
    {
        return m_userEmail;
    }

    public void setTextReview(String i_textReview)
    {
        this.m_textReview = i_textReview;
    }

    public void setRating(byte i_rating)
    {
        this.m_rating = i_rating;
    }

    public void setUserEmail(String i_userEmail)
    {
        this.m_userEmail = i_userEmail;
    }
}
