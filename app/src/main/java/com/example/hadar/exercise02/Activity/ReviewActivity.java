package com.example.hadar.exercise02.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.Review;
import com.example.hadar.exercise02.model.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewActivity extends Activity
{
    private final String TAG = "ReviewActivity";
    private final int NEW_RATING = -1;
    private Movie m_movie;
    private String m_key;
    private UserDetails m_user;
    private int prevRating = NEW_RATING;
    private TextView reviewText;
    private RatingBar userRating;
    private Review m_userReview;
    private DatabaseReference m_movieRef;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_review);

        m_key = getIntent().getStringExtra("Key");
        m_movie = (Movie) getIntent().getSerializableExtra("Movie");
        m_user = (UserDetails) getIntent().getSerializableExtra("UserDetails");

        reviewText = findViewById(R.id.new_user_review);
        userRating = findViewById(R.id.new_user_rating);


        m_movieRef = FirebaseDatabase.getInstance().getReference("Movie/" + m_key);

        m_movieRef.child("/reviews/" +  FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange(Review) >> " + snapshot.getKey());

                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            reviewText.setText(review.getM_textReview());
                            userRating.setRating(review.getM_rating());
                            prevRating = review.getM_rating();
                        }

                        Log.e(TAG, "onDataChange(Review) <<");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
                    }
                });

        Log.e(TAG, "onCreate() <<");

    }

    private boolean checkIfReviewSubmissionIsValidAndToastIfNot()
    {
        boolean isValidReview;

        if((int)userRating.getRating() == 0)
        {
            Toast.makeText(ReviewActivity.this, "Please select your rating.", Toast.LENGTH_LONG).show();
            isValidReview = false;
        }

        else
        {
            isValidReview = true;
        }

        return isValidReview;
    }

    private void handleNewReview()
    {
        m_movie.incrementReviewsCount();
        m_movie.incrementRating((int)userRating.getRating());
    }

    private void handleEditedReview()
    {
        m_movie.incrementRating((int) userRating.getRating() - prevRating);
    }

    private void createUserReview()
    {
        m_userReview = new Review(reviewText.getText().toString(), (int)userRating.getRating(),
                                  m_user.getUserEmail());
    }

    private void updateMovieOnDatabase()
    {
        m_movieRef.child("m_reviewsCount").setValue(m_movie.getM_reviewsCount());
        m_movieRef.child("m_averageRating").setValue(m_movie.getM_averageRating());
        m_movieRef.child("m_rating").setValue(m_movie.getM_rating());
        m_movieRef.child("reviews").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(m_userReview);
    }

    private void moveToReservationSummaryActivity()
    {
        Intent intent = new Intent(getApplicationContext(),ReservationSummaryActivity.class);
        intent.putExtra("Movie", m_movie);
        intent.putExtra("Key", m_key);
        intent.putExtra("UserDetails",m_user);
        startActivity(intent);
        finish();
    }

    public void onSubmitClick(View i_view)
    {
        Log.e(TAG, "onSubmitClick() >>");

        if(checkIfReviewSubmissionIsValidAndToastIfNot())
        {
            if (prevRating == NEW_RATING)
            {
                handleNewReview();
            }

            else
            {
                handleEditedReview();
            }

            m_movie.updateRating();
            createUserReview();
            updateMovieOnDatabase();
            super.onBackPressed();
        }

        Log.e(TAG, "onSubmitClick() <<");
    }
}

/*m_movieRef.runTransaction(new Transaction.Handler()
        {
            @Override
            public Transaction.Result doTransaction(MutableData i_mutableData)
            {
                Log.e(TAG, "doTransaction() >>" );
                Movie movie = i_mutableData.getValue(Movie.class);

                if (movie == null )
                {
                    Log.e(TAG, "doTransaction() << movie is null" );
                    return Transaction.success(i_mutableData);
                }

                if (prevRating == -1)
                {
                    // Increment the review count and rating only in case the user enters a new review
                    movie.incrementReviewsCount();
                    movie.incrementRating((int)userRating.getRating());
                }
                else
                {
                    movie.incrementRating((int) userRating.getRating() - prevRating);
                }

                movie.updateRating();
                i_mutableData.setValue(movie);
                Log.e(TAG, "doTransaction() << movie was set");
                return Transaction.success(i_mutableData);

            }

            @Override
            public void onComplete(DatabaseError i_databaseError, boolean i_committed, DataSnapshot i_dataSnapshot)
            {

                Log.e(TAG, "onComplete() >>" );

                if (i_databaseError != null) {
                    Log.e(TAG, "onComplete() << Error:" + i_databaseError.getMessage());
                    return;
                }

                if (i_committed) {
                    Review review = new Review(
                            reviewText.getText().toString(),
                            (int)userRating.getRating(),
                            m_user.getUserEmail());

                    m_movieRef.child("/reviews/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(review);
                }


                Intent intent = new Intent(getApplicationContext(),ReservationSummaryActivity.class);
                intent.putExtra("Movie", m_movie);
                intent.putExtra("Key", m_key);
                intent.putExtra("UserDetails",m_user);
                startActivity(intent);
                finish();

                Log.e(TAG, "onComplete() <<" );
            }
        });*/