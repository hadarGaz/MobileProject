package com.example.hadar.exercise02.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Movie implements Serializable
{
    private String m_name;
    private String m_date;
    private double m_price;
    private String m_trailerURL;
    private String m_genre;
    private String m_cinemaLocation;
    private String m_thumbImage;
    private int m_rating;
    private int m_reviewsCount;
    private String m_movieDescription;
    private ImageView m_movieImage;

    public enum eGenre
    {
        Comedy, SciFi, Horror, Romance,
        Action, Thriller, Drama, Mystery,
        Crime, Animation, Adventure, Fantasy
    }
    public Movie() {
    }

    public String getM_name() {
        return m_name;
    }

    public String getM_cinemaLocation() {
        return m_cinemaLocation;
    }

    public String getM_movieDescription() {
        return m_movieDescription;
    }

    public String getM_date() {
        return m_date;
    }

    public double getM_price() {
        return m_price;
    }

    public String getM_trailerURL() {
        return m_trailerURL;
    }

    public String getM_genre() {
        return m_genre;
    }

    public int getM_rating() {
        return m_rating;
    }

    public String getM_thumbImage()
    {

        return m_thumbImage;
    }

    public int getM_reviewsCount()
    {

        return m_reviewsCount;
    }

    /*
        public void setDate(Date i_date) throws Exception
        {
            Date currentDate = Calendar.getInstance().getTime();

            if(i_date.before(currentDate))
                throw new Exception("Error - Date already passed\n");

            else
                m_date = i_date;
        }

        public void setPrice(double i_price) throws Exception
        {
            if(i_price < 0)
                throw new Exception("Negative price is illegal");

            else
                m_price = i_price;

        }

    */



}
