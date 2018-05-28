package com.example.hadar.AcadeMovie.Analytics;

import android.content.Context;
import android.os.Bundle;

import com.example.hadar.AcadeMovie.model.Movie;
import com.example.hadar.AcadeMovie.model.UserDetails;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;
import com.appsee.Appsee;

public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager m_instance = null;
    private FirebaseAnalytics m_firebaseAnalytics;

    private AnalyticsManager()
    {
    }

    public static AnalyticsManager getInstance()
    {

        if (m_instance == null)
        {
            m_instance = new AnalyticsManager();
        }
        return (m_instance);
    }

    public void init(Context context)
    {
        m_firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Appsee.start();
    }

    public void setUserID(String i_id)
    {
        m_firebaseAnalytics.setUserId(i_id);
        Appsee.setUserId(i_id);

    }

    public void setUserProperty(UserDetails i_userDetails)
    {
        m_firebaseAnalytics.setUserProperty("user_email",i_userDetails.getUserEmail());
        m_firebaseAnalytics.setUserProperty("user_name",i_userDetails.getUserName());
        m_firebaseAnalytics.setUserProperty("user_purchase_count",String.valueOf(i_userDetails.getMoviesPurchaseMap().size()));
        m_firebaseAnalytics.setUserProperty("user_purchase_amount",String.valueOf(i_userDetails.getM_totalPurchaseAmount()));
        m_firebaseAnalytics.setUserProperty("user_total_tickets_count",String.valueOf(i_userDetails.getM_totalTicketsCount()));
    }

    public void trackSignupEvent(String i_signUpMethod)
    {

        String eventName = "signup";
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, i_signUpMethod);
        m_firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);

        //AppSee
        Appsee.addEvent(eventName);
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("signup method", i_signUpMethod);
        Appsee.addEvent(eventName,eventParams2);
    }

    public void trackSearchEvent(String i_searchString)
    {

        String eventName = "search";

        //Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, i_searchString);
        m_firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH,params);

        //AppSee
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("search term", i_searchString);
        Appsee.addEvent(eventName,eventParams2);

    }

    public void trackMovieDetailsEvent(Movie i_movie)
    {
        String eventName = "Movie_details_view";
        Bundle params = new Bundle();

        params.putString("movie_name", i_movie.getM_name());
        params.putDouble("movie_price", i_movie.getM_price());
        params.putString("movie_genre", i_movie.getM_genre());
        params.putDouble("movie_rating", i_movie.getM_averageRating());

        m_firebaseAnalytics.logEvent(eventName,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("movie_name", i_movie.getM_name());
        eventParams.put("movie_price", i_movie.getM_price());
        eventParams.put("movie_genre", i_movie.getM_genre());
        eventParams.put("movie_rating", i_movie.getM_averageRating());

        Appsee.addEvent(eventName,eventParams);
    }

    public void trackPurchase(Movie i_movie)
    {

        String eventName = "purchase";
        Bundle params = new Bundle();
        params.putString("movie_name", i_movie.getM_name());
        params.putDouble("movie_price",i_movie.getM_price());
        params.putString("movie_genre", i_movie.getM_genre());
        params.putDouble("movie_rating", i_movie.getM_averageRating());
        m_firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("movie_name", i_movie.getM_name());
        eventParams.put("movie_price", i_movie.getM_price());
        eventParams.put("movie_genre", i_movie.getM_genre());
        eventParams.put("movie_rating",i_movie.getM_averageRating());

        Appsee.addEvent(eventName,eventParams);
    }

    public void trackMovieNewRating(Movie i_movie ,int i_userRating)
    {

        String eventName = "movie_new_rating";
        Bundle params = new Bundle();

        params.putString("movie_name", i_movie.getM_name());
        params.putDouble("movie_reviews_count",i_movie.getM_reviewsCount());
        params.putDouble("movie_total_rating",i_movie.getM_averageRating());
        params.putDouble("movie_user_rating",i_userRating);

        m_firebaseAnalytics.logEvent(eventName,params);

        //AppSee
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("movie_name", i_movie.getM_name());
        eventParams2.put("movie_reviews_count",i_movie.getM_reviewsCount());
        eventParams2.put("movie_total_rating",i_movie.getM_averageRating());
        eventParams2.put("movie_user_rating",i_userRating);

        Appsee.addEvent(eventName,eventParams2);
    }

    public void trackMovieEditRating(Movie i_movie ,int i_userRating,int i_prevRating)
    {

        String eventName = "movie_edit_rating";
        Bundle params = new Bundle();

        params.putString("movie_name", i_movie.getM_name());
        params.putDouble("movie_reviews_count",i_movie.getM_reviewsCount());
        params.putDouble("movie_total_rating",i_movie.getM_averageRating());
        params.putDouble("movie_user_new_rating",i_userRating);
        params.putDouble("movie_user_prev_rating",i_prevRating);

        m_firebaseAnalytics.logEvent(eventName,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("movie_name", i_movie.getM_name());
        eventParams.put("movie_reviews_count",i_movie.getM_reviewsCount());
        eventParams.put("movie_total_rating",i_movie.getM_averageRating());
        eventParams.put("movie_user_new_rating",i_userRating);
        eventParams.put("movie_user_prev_rating",i_prevRating);

        Appsee.addEvent(eventName,eventParams);
    }

    public void trackRepeatPurchase(Movie i_movie)
    {

        String eventName = "RepeatPurchase";
        Bundle params = new Bundle();
        params.putString("movie_name", i_movie.getM_name());
        params.putDouble("movie_price",i_movie.getM_price());
        params.putString("movie_genre", i_movie.getM_genre());
        params.putDouble("movie_rating", i_movie.getM_averageRating());

        m_firebaseAnalytics.logEvent(eventName,params);

        //AppSee
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("movie_name", i_movie.getM_name());
        eventParams.put("movie_price", i_movie.getM_price());
        eventParams.put("movie_genre", i_movie.getM_genre());
        eventParams.put("movie_rating", i_movie.getM_averageRating());

        Appsee.addEvent(eventName,eventParams);
    }
}
