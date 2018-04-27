package com.example.hadar.exercise02.model;

import com.example.hadar.exercise02.model.Movie;

public class MovieWithKey
{
    private Movie m_movie;
    private String m_key;

    public MovieWithKey(Movie i_movie, String i_key)
    {
        m_movie = i_movie;
        m_key = i_key;
    }

    public void setMovie(Movie i_movie)
    {
        m_movie = i_movie;
    }

    public void setKey(String i_key)
    {
        m_key = i_key;
    }

    public Movie getMovie()
    {
        return m_movie;
    }

    public String getKey()
    {
        return m_key;
    }
}
