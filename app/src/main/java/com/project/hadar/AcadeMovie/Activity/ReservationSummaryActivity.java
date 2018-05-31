package com.project.hadar.AcadeMovie.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.project.hadar.AcadeMovie.Analytics.AnalyticsManager;
import com.project.hadar.AcadeMovie.R;
import com.project.hadar.AcadeMovie.adapter.ReviewsAdapter;
import com.project.hadar.AcadeMovie.model.Movie;
import com.project.hadar.AcadeMovie.model.ProfileWidget;
import com.project.hadar.AcadeMovie.model.Review;
import com.project.hadar.AcadeMovie.model.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservationSummaryActivity extends AppCompatActivity
{
    private static final String TAG ="ReservationSummaryActivity";
    private static final int MAX_CHAR = 5;
    private Movie m_movie;
    private String m_key;
    private UserDetails m_userDetails;
    private TextView m_movieNameTextView;
    private TextView m_dateTextView;
    private TextView m_CinemaLocationTextView;
    private TextView m_genreTextView;
    private TextView m_textViewTicketType1;
    private TextView m_textViewTicketType2;
    private TextView m_textViewTicketType3;
    private TextView m_textViewTotalPrice;
    private TextView m_textViewReviewCount;
    private ImageButton m_profileWidgetImageButton;
    private List<Review> m_reviewsList =  new ArrayList<>();
    private RecyclerView m_recyclerViewMovieReviews;
    private ImageView m_imageViewMoviePic;
    private RatingBar m_ratingBarForMovie;
    private AnalyticsManager m_analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        Log.e(TAG, "onCreate() >>");

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_reservation_summary);

        findViews();
        getMovieIntent();

        getUserDetailsAndContinueOnCreate();

        setReviewsAdapter();
        setListenerToReview();

        Log.e(TAG, "onCreate() <<");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setListenerToReview();
    }

    @SuppressWarnings("ConstantConditions")
    private void getUserDetailsAndContinueOnCreate()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener()
                                      {
                                          @Override
                                          public void onDataChange (DataSnapshot dataSnapshot)
                                          {
                                              m_userDetails = dataSnapshot.getValue(UserDetails.class);
                                              displayUserImage();
                                              setMovieDetails();
                                          }
                                          @Override
                                          public void onCancelled (DatabaseError d)
                                          {

                                          }
                                      }
        );
    }

    private void setReviewsAdapter()
    {
        Log.e(TAG, "setReviewsAdapter() >>");

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(m_reviewsList);
        m_recyclerViewMovieReviews.setAdapter(reviewsAdapter);

        Log.e(TAG, "setReviewsAdapter() <<");
    }

    //Note for me: Check if can be replaced with List.contains()
    private boolean doesReviewListContainAReview(Review i_review)
    {
        boolean reviewFound = false;

        for(int i = 0; i<m_reviewsList.size(); ++i)
        {
            if(m_reviewsList.get(i).getM_userEmail() != null &&
               m_reviewsList.get(i).getM_userEmail().equals(i_review.getM_userEmail()))
            {
                reviewFound = true;
                break;
            }
        }

        return reviewFound;
    }

    private void setListenerToReview()
    {
        Log.e(TAG, "setListenerToReview() >>");

        m_reviewsList.clear();
        DatabaseReference movieReviewsRef = FirebaseDatabase.getInstance().getReference("Movie/" + m_key + "/reviews");
        movieReviewsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {

                Log.e(TAG, "onDataChange() >> Movie/" );

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (!(doesReviewListContainAReview(review)))
                    {
                        m_reviewsList.add(review);
                    }
                }
                m_recyclerViewMovieReviews.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange(Review) <<");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });

        Log.e(TAG, "setListenerToReview() <<");

    }

    private void findViews()
    {
        Log.e(TAG, "findViews() >>");

        m_dateTextView = findViewById(R.id.textViewDate);
        m_movieNameTextView = findViewById(R.id.textViewMovieName);
        m_CinemaLocationTextView = findViewById(R.id.textViewCinemaLocation);
        m_genreTextView = findViewById(R.id.textViewGenre);
        m_profileWidgetImageButton = findViewById(R.id.profile_widget);
        m_recyclerViewMovieReviews = findViewById(R.id.MovieReview);
        m_recyclerViewMovieReviews.setHasFixedSize(true);
        m_recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerViewMovieReviews.setItemAnimator(new DefaultItemAnimator());
        m_imageViewMoviePic =  findViewById(R.id.imageViewMoviePic);
        m_textViewTicketType1 = findViewById(R.id.textViewTicketType1);
        m_textViewTicketType2 = findViewById(R.id.textViewTicketType2);
        m_textViewTicketType3 = findViewById(R.id.textViewTicketType3);
        m_textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        m_ratingBarForMovie = findViewById(R.id.ratingBarForMovie);
        m_textViewReviewCount = findViewById(R.id.textViewReviewCount);

        LayerDrawable stars = (LayerDrawable) m_ratingBarForMovie.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        Log.e(TAG, "findViews() <<");

    }

    private void getMovieIntent()
    {
        Log.e(TAG, "getMovieIntent() >>");

        m_key = getIntent().getStringExtra("Key");
        m_movie = (Movie) getIntent().getSerializableExtra("Movie");

        Log.e(TAG, "getMovieIntent() <<");
    }

    private void setMovieDetails()
    {
        Log.e(TAG, "setMovieDetails() >>");

        m_textViewTicketType1.setVisibility(View.INVISIBLE);
        m_textViewTicketType2.setVisibility(View.INVISIBLE);
        m_textViewTicketType3.setVisibility(View.INVISIBLE);
        m_movieNameTextView.setText(m_movie.getM_name());
        m_dateTextView.setText(m_movie.getM_date());
        m_CinemaLocationTextView.setText(m_movie.getM_cinemaLocation());
        m_genreTextView.setText(m_movie.getM_genre());
        m_ratingBarForMovie.setRating(m_movie.getM_averageRating());
        m_textViewReviewCount.setText("(" + m_movie.getM_reviewsCount() + ")");
        setMovieImage();
        setMoviePurchase();

        Log.e(TAG, "setMovieDetails() <<");
    }

    @SuppressLint("SetTextI18n")
    private void setMoviePurchase()
    {
        Log.e(TAG, "setMoviePurchase() >>");

        int value;
        m_textViewTotalPrice.setText(limitStrToMaxChar(String.valueOf(m_userDetails.getMoviesPurchaseMap().get(m_key).getM_purchaseAmount())) + "$");
        Map<String, Integer> purchaseMap =  m_userDetails.getMoviesPurchaseMap().get(m_key).getM_mapOfTypeTicketsAndQuantity();

        if((value= purchaseMap.get("Standard")) != 0)
        {
            m_textViewTicketType1.setText(String.valueOf(value) + " Standard");
            m_textViewTicketType1.setVisibility(View.VISIBLE);
        }
        if((value= purchaseMap.get("Student")) != 0)
        {
            m_textViewTicketType2.setText(String.valueOf(value) + " Student");
            m_textViewTicketType2.setVisibility(View.VISIBLE);
        }
        if((value= purchaseMap.get("Soldier")) != 0)
        {
            m_textViewTicketType3.setText(String.valueOf(value) + " Soldier");
            m_textViewTicketType3.setVisibility(View.VISIBLE);
        }

        Log.e(TAG, "setMoviePurchase() <<");
    }

    private void setMovieImage()
    {
        Log.e(TAG, "setMovieImage() >>");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Movie Pictures/" + m_movie.getM_thumbImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        Log.e(TAG, "pic src= " + uri.toString());
                        Glide.with(getApplicationContext()).load(uri.toString()).into(m_imageViewMoviePic);
                    }
                });

        Log.e(TAG, "setMovieImage() <<");
    }

    public void onAddReviewClick(View i_View)
    {
        Log.e(TAG, "onAddReviewClick() >>");

        Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
        intent.putExtra("Movie", m_movie);
        intent.putExtra("Key", m_key);
        intent.putExtra("UserDetails", m_userDetails);
        startActivity(intent);

        Log.e(TAG, "onAddReviewClick() <<");
    }

    public void onClickProfileWidgetImageButton(View i_View)
    {
        Log.e(TAG, "onClickProfileWidgetImageButton() >>");

        ProfileWidget.onClickProfileWidget(this, m_profileWidgetImageButton, m_userDetails);

        Log.e(TAG, "onClickProfileWidgetImageButton() <<");

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goToCinemaMainActivity();
    }

    private void goToCinemaMainActivity()
    {
        Intent cinemaMainActivity = new Intent(getApplicationContext(), CinemaMainActivity.class);
        startActivity(cinemaMainActivity);
        finish();
    }

    private void displayUserImage()
    {
        Log.e(TAG, "displayUserImage() >>");

        ProfileWidget.displayUserImage(getApplicationContext(), m_profileWidgetImageButton, m_userDetails);

        Log.e(TAG, "displayUserImage() <<");

    }

    public void onBuyMoreTicketsClick(View i_view)
    {
        Log.e(TAG, "onBuyMoreTicketsClick() >>");

        m_analyticsManager.trackRepeatPurchase(m_movie);
        m_analyticsManager.setUserProperty(m_userDetails);
        Intent SelectTicketsIntent = new Intent(getApplicationContext(), SelectTicketsActivity.class);
        SelectTicketsIntent.putExtra("Movie", m_movie);
        SelectTicketsIntent.putExtra("Key", m_key);
        startActivity(SelectTicketsIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        Log.e(TAG, "onBuyMoreTicketsClick() <<");
    }

    private String limitStrToMaxChar(String i_Str)
    {
        String resStr = String.valueOf(i_Str);
        int maxLength = (resStr.length() < MAX_CHAR)?resStr.length():MAX_CHAR;
        return (resStr.substring(0, maxLength));
    }
}