package com.example.hadar.exercise02.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.adapter.ReviewsAdapter;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.ProfileWidget;
import com.example.hadar.exercise02.model.Purchase;
import com.example.hadar.exercise02.model.Review;
import com.example.hadar.exercise02.model.UserDetails;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
    private Movie m_movie;
    private String m_key;
    private UserDetails m_userDetails;
    private FloatingActionButton m_addReviewButton;
    private TextView m_movieNameTextView;
    private TextView m_dateTextView;
    private TextView m_CinemaLocationTextView;
    private TextView m_jannerTextView;
    private TextView m_movieDescriptionTextView;
    private TextView m_textViewTicketType1;
    private TextView m_textViewTicketType2;
    private TextView m_textViewTicketType3;
    private TextView m_textViewTotalPrice;
    private ImageButton m_profileWidgetImageButton;
    private List<Review> m_reviewsList =  new ArrayList<>();
    private RecyclerView m_recyclerViewMovieReviews;
    private DatabaseReference m_movieReviewsRef;
    private ImageView m_imageViewMoviePic;

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

    private void getUserDetailsAndContinueOnCreate()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener()
                                      {
                                          @Override
                                          public void onDataChange (DataSnapshot dataSnapshot) {
                                              m_userDetails = dataSnapshot.getValue(UserDetails.class);
                                              displayUserImage();
                                              setMovieDetails();
                                          }
                                          @Override
                                          public void onCancelled (DatabaseError d) {

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

    private void setListenerToReview()
    {
        Log.e(TAG, "setListenerToReview() >>");

        m_movieReviewsRef = FirebaseDatabase.getInstance().getReference("Movie/" + m_key +"/reviews");

        m_movieReviewsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {

                Log.e(TAG, "onDataChange() >> Movie/" );

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
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

        Log.e(TAG, "setListenerToReview() <<");

    }

    private void findViews()
    {
        Log.e(TAG, "findViews() >>");

        m_dateTextView = findViewById(R.id.textViewDate);
        m_movieNameTextView = findViewById(R.id.textViewMovieName);
        m_CinemaLocationTextView = findViewById(R.id.textViewCinemaLocation);
        m_jannerTextView = findViewById(R.id.textViewGenre);
        m_movieDescriptionTextView = findViewById(R.id.textViewMovieDescription);
        m_profileWidgetImageButton = findViewById(R.id.profile_widget);
        m_recyclerViewMovieReviews = findViewById(R.id.MovieReview);
        m_recyclerViewMovieReviews.setHasFixedSize(true);
        m_recyclerViewMovieReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerViewMovieReviews.setItemAnimator(new DefaultItemAnimator());
        m_addReviewButton = findViewById(R.id.buttonNewReview);
        m_imageViewMoviePic =  findViewById(R.id.imageViewMoviePic);
        m_textViewTicketType1 = findViewById(R.id.textViewTicketType1);
        m_textViewTicketType2 = findViewById(R.id.textViewTicketType2);
        m_textViewTicketType3 = findViewById(R.id.textViewTicketType3);
        m_textViewTotalPrice = findViewById(R.id.textViewTotalPrice);


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
        m_dateTextView.setText(m_movie.getM_date().toString());
        m_CinemaLocationTextView.setText(m_movie.getM_cinemaLocation());
        m_jannerTextView.setText(m_movie.getM_genre().toString());
        m_movieDescriptionTextView.setText(m_movie.getM_movieDescription());

        setMovieImage();
        setMoviePurchase();

        Log.e(TAG, "setMovieDetails() <<");
    }

    private void setMoviePurchase()
    {
        Log.e(TAG, "setMoviePurchase() >>");

        int value=0;
        m_textViewTotalPrice.setText(String.valueOf(m_userDetails.getMoviesPurchaseMap().get(m_key).getM_purchaseAmount()) + "$");
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
                    public void onSuccess(Uri uri) {
                        Log.e(TAG,"pic src= "+ uri.toString());
                        Glide.with(ReservationSummaryActivity.this)
                                .load(uri.toString())
                                .into(m_imageViewMoviePic);
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

    private void displayUserImage()
    {
        Log.e(TAG, "displayUserImage() >>");

        ProfileWidget.displayUserImage(this, m_profileWidgetImageButton, m_userDetails);

        Log.e(TAG, "displayUserImage() <<");

    }

    public void onBuyMoreTicketsClick(View i_view)
    {
        Log.e(TAG, "onBuyMoreTicketsClick() >>");

        Intent SelectTicketsIntent = new Intent(getApplicationContext(), SelectTicketsActivity.class);
        SelectTicketsIntent.putExtra("Movie", m_movie);
        SelectTicketsIntent.putExtra("Key", m_key);
        startActivity(SelectTicketsIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        Log.e(TAG, "onBuyMoreTicketsClick() <<");
    }
}
