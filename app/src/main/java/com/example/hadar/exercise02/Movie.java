package com.example.hadar.exercise02;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class Movie
{
    private String m_name;
    private Date m_date;
    private double m_price;
    private URL m_trailerURL;
    private eGenre m_genre;
    private URL m_pictureURL;
    private String m_cinema;
    private String m_thumbImage;
    private int m_rating;
    private int m_reviewsCount;

    public enum eGenre
    {
        Comedy, SciFi, Horror, Romance,
        Action, Thriller, Drama, Mystery,
        Crime, Animation, Adventure, Fantasy
    }

    public Movie(String i_name)
    {
        m_name = i_name;
    }

    public void setDate(Date i_date) throws Exception
    {
        Date currentDate = Calendar.getInstance().getTime();

        if(i_date.after(currentDate))
            throw new Exception("Future date is illegal");

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

    public void setTrailerURL(URL i_trailerURL)
    {
        m_trailerURL = i_trailerURL;
    }

    public void setGenre(eGenre i_genre)
    {
        m_genre = i_genre;
    }

    public void setPictureURL(URL i_pictureURL)
    {
        m_pictureURL = i_pictureURL;
    }

    public void setCinema(String i_cinema)
    {
        m_cinema = i_cinema;
    }

    public void setThumbImage(String i_thumbImage)
    {
        m_thumbImage = i_thumbImage;
    }

    public void setRating(int i_rating)
    {
        m_rating = i_rating;
    }

    public int getRating()
    {
        return m_rating;
    }

    public String getName()
    {
        return m_name;
    }

    public Date getDate()
    {
        return m_date;
    }

    public double getPrice()
    {
        return m_price;
    }

    public URL getTrailerURL()
    {
        return m_trailerURL;
    }

    public eGenre getGenre()
    {
        return m_genre;
    }

    public URL getPictureURL()
    {
        return m_pictureURL;
    }

    public String getCinema()
    {
        return m_cinema;
    }

    public String getThumbImage()
    {
        return m_thumbImage;
    }

    public int getReviewsCount()
    {
        return m_reviewsCount;
    }
}
