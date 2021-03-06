package com.project.hadar.AcadeMovie.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.hadar.AcadeMovie.Analytics.AnalyticsManager;
import com.project.hadar.AcadeMovie.R;
import com.project.hadar.AcadeMovie.Model.Movie;
import com.project.hadar.AcadeMovie.Model.Review;
import com.project.hadar.AcadeMovie.Model.UserDetails;
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
    private String m_userEmail;
    private int prevRating = NEW_RATING;
    private TextView m_textViewReview;
    private RatingBar m_rating;
    private Review m_userReview;
    private DatabaseReference m_movieRef;
    private AnalyticsManager m_analyticsManager = AnalyticsManager.getInstance();

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_review);

        String movieKey = getIntent().getStringExtra("Key");
        m_movie = (Movie) getIntent().getSerializableExtra("Movie");
        m_userEmail = getIntent().getStringExtra("UserEmail");

        m_textViewReview = findViewById(R.id.new_user_review);
        m_rating = findViewById(R.id.new_user_rating);


        m_movieRef = FirebaseDatabase.getInstance().getReference("Movie/" + movieKey);

        m_movieRef.child("/reviews/" +  FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange(Review) >> " + snapshot.getKey());

                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            m_textViewReview.setText(review.getM_textReview());
                            m_rating.setRating(review.getM_rating());
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

        if((int) m_rating.getRating() == 0)
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
        m_movie.incrementRating((int) m_rating.getRating());
    }

    private void handleEditedReview()
    {
        m_movie.incrementRating((int) m_rating.getRating() - prevRating);
    }

    private void createUserReview()
    {
        m_userReview = new Review(m_textViewReview.getText().toString(), (int) m_rating.getRating(),
                                  m_userEmail);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateMovieOnDatabase()
    {
        m_movieRef.child("m_reviewsCount").setValue(m_movie.getM_reviewsCount());
        m_movieRef.child("m_averageRating").setValue(m_movie.getM_averageRating());
        m_movieRef.child("m_rating").setValue(m_movie.getM_rating());
        m_movieRef.child("reviews").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(m_userReview);
    }

    public void onSubmitClick(View i_view)
    {
        Log.e(TAG, "onSubmitClick() >>");

        if(checkIfReviewSubmissionIsValidAndToastIfNot())
        {
            if (prevRating == NEW_RATING)
            {
                handleNewReview();
                m_analyticsManager.trackMovieNewRating(m_movie,(int) m_rating.getRating());
            }

            else
            {
                handleEditedReview();
                m_analyticsManager.trackMovieEditRating(m_movie,(int) m_rating.getRating(),prevRating);
            }

            m_movie.updateRating();
            createUserReview();
            updateMovieOnDatabase();
            super.onBackPressed();
        }

        Log.e(TAG, "onSubmitClick() <<");
    }
}