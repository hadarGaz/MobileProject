package com.example.hadar.exercise02.model;

import android.widget.ImageView;

import java.io.Serializable;

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
    private int m_averageRating;
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

    public int getM_averageRating()
    {
        return m_averageRating;
    }

    public int compareRating(Movie i_movie)
    {
        int result;

        if(m_rating - i_movie.m_rating < 0)
        {
            result = -1;
        }

        else if (m_rating - i_movie.m_rating > 0)
        {
            result = 1;
        }

        else
        {
            result = 0;
        }

        return result;
    }

    public String getM_thumbImage()
    {

        return m_thumbImage;
    }

    public int getM_reviewsCount()
    {

        return m_reviewsCount;
    }

    public void incrementReviewsCount() { m_reviewsCount++;}

    public void incrementRating(int i_rating)
    {
        m_rating += i_rating;
    }

    public void updateRating()
    {
        if(m_reviewsCount != 0)
        {
            m_averageRating = m_rating / m_reviewsCount;
        }
    }
}