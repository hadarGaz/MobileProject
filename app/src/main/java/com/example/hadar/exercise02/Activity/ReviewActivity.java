package com.example.hadar.exercise02.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.Review;
import com.example.hadar.exercise02.model.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class ReviewActivity extends Activity {

    private final String TAG = "ReviewActivity";
    private Movie m_movie;
    private String m_key;
    private UserDetails m_user;
    private int prevRating = -1;
    private TextView userReview;
    private RatingBar userRating;
    private DatabaseReference m_movieRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        m_key = getIntent().getStringExtra("Key");
        m_movie = (Movie) getIntent().getSerializableExtra("Movie");
        m_user = (UserDetails) getIntent().getSerializableExtra("UserDetails");

        userReview = findViewById(R.id.new_user_review);
        userRating = findViewById(R.id.new_user_rating);


        m_movieRef = FirebaseDatabase.getInstance().getReference("Movie/" + m_key);

        m_movieRef.child("/reviews/" +  FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange(Review) >> " + snapshot.getKey());

                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            userReview.setText(review.getM_textReview());
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

    public void onSubmitClick(View v) {

        Log.e(TAG, "onSubmitClick() >>");


        m_movieRef.runTransaction(new Transaction.Handler()
        {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData)
            {
                Log.e(TAG, "doTransaction() >>" );
                Movie movie = mutableData.getValue(Movie.class);

                if (movie == null )
                {
                    Log.e(TAG, "doTransaction() << movie is null" );
                    return Transaction.success(mutableData);
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
                mutableData.setValue(movie);
                Log.e(TAG, "doTransaction() << movie was set");
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                Log.e(TAG, "onComplete() >>" );

                if (databaseError != null) {
                    Log.e(TAG, "onComplete() << Error:" + databaseError.getMessage());
                    return;
                }

                if (committed) {
                    Review review = new Review(
                            userReview.getText().toString(),
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
        });



        Log.e(TAG, "onSubmitClick() <<");
    }

}

