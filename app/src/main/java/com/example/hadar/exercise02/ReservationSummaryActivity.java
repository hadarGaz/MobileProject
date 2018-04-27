package com.example.hadar.exercise02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReservationSummaryActivity extends AppCompatActivity
{
    public final String TAG ="ReservationSummaryActivity";

    private Movie m_movie;
    private String m_key;
    private UserDetails m_user;

    private TextView m_movieNameTextView;
    private TextView m_dateTextView;
    private TextView m_CinemaLocationTextView;
    private TextView m_jannerTextView;
    private TextView m_movieDescriptionTextView;

    private List<Review> m_reviewsList =  new ArrayList<>();

    private RecyclerView m_recyclerViewMovieReviews;
    private DatabaseReference m_movieReviewsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_summary);

        findViews();
        getMovieIntent();
        setMovieDetails();

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(m_reviewsList);
        m_recyclerViewMovieReviews.setAdapter(reviewsAdapter);

        m_movieReviewsRef = FirebaseDatabase.getInstance().getReference("Movie/" + m_key +"/reviews");

        m_movieReviewsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange() >> Movie/" );

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    m_reviewsList.add(review);
                }
                m_recyclerViewMovieReviews.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange(Review) <<");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });
        Log.e(TAG, "onCreate() <<");
    }

    private void findViews()
    {
        m_dateTextView = findViewById(R.id.textViewDate);
        m_movieNameTextView = findViewById(R.id.textViewMovieName);
        m_CinemaLocationTextView = findViewById(R.id.textViewCinemaLocation);
        m_jannerTextView = findViewById(R.id.textViewJanner);
        m_movieDescriptionTextView = findViewById(R.id.textViewMovieDescription);

        m_recyclerViewMovieReviews = findViewById(R.id.MovieReview);
        m_recyclerViewMovieReviews.setHasFixedSize(true);
        m_recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerViewMovieReviews.setItemAnimator(new DefaultItemAnimator());
    }

    private void getMovieIntent()
    {
        m_key = getIntent().getStringExtra("key");
        m_movie = getIntent().getParcelableExtra("movie");
        m_user = getIntent().getParcelableExtra("user");
    }
    private void setMovieDetails()
    {
        m_movieNameTextView.setText(m_movie.getName());
        m_dateTextView.setText(m_movie.getDate().toString());
        //m_CinemaLocationTextView.setText(m_movie.get
        m_jannerTextView.setText(m_movie.getGenre().toString());
        //m_movieDescriptionTextView.setText(m_movie.get
    }

}
