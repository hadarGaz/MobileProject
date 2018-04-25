package com.example.hadar.exercise02;


public class Review {
    private String textReview;
    private byte rating;
    private String userName;


    public Review() {}


    public Review(String i_textReview, byte i_rating, String i_userName)
    {
        this.textReview = i_textReview;
        this.rating = i_rating;
        this.userName = i_userName;
    }


    public String getTextReview()
    {
        return textReview;
    }

    public byte getRating()
    {
        return rating;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setTextReview(String i_textReview)
    {
        this.textReview = i_textReview;
    }

    public void setRating(byte i_rating)
    {
        this.rating = i_rating;
    }

    public void setUserName(String i_userName)
    {
        this.userName = i_userName;
    }
}
