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

    public void setName(String i_name) throws Exception
    {
        if(i_name == null)
            throw new Exception("Movie name cannot be null");

        else
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

    public void setTrailerUrl(URL i_trailerURL)
    {
        m_trailerURL = i_trailerURL;
    }

    public void setGenre(eGenre i_genre)
    {
        m_genre = i_genre;
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
}
