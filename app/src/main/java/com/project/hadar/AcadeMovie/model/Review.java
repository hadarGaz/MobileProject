package com.project.hadar.AcadeMovie.model;


import java.io.Serializable;

public class Review implements Serializable {
    private String m_textReview;
    private int m_rating;
    private String m_userEmail;


    public Review() {}


    public Review(String i_textReview, int i_rating, String i_userName)
    {
        this.m_textReview = i_textReview;
        this.m_rating = i_rating;
        this.m_userEmail = i_userName;
    }

    public String getM_textReview() {
        return m_textReview;
    }

    public int getM_rating() {
        return m_rating;
    }

    public String getM_userEmail() {
        return m_userEmail;
    }
/*
    public String getTextReview()
    {
        return m_textReview;
    }

    public int getRating()
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
    */
}
