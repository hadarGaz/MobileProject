package com.project.hadar.AcadeMovie.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.bumptech.glide.Glide;
import com.project.hadar.AcadeMovie.Analytics.AnalyticsManager;
import com.project.hadar.AcadeMovie.Model.BillingManager;
import com.project.hadar.AcadeMovie.Model.MoviePurchase;
import com.project.hadar.AcadeMovie.R;
import com.project.hadar.AcadeMovie.Model.Movie;
import com.project.hadar.AcadeMovie.Model.ProfileWidget;
import com.project.hadar.AcadeMovie.Model.Purchase;
import com.project.hadar.AcadeMovie.Model.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTicketsActivity extends YouTubeBaseActivity implements BillingManager.BillingUpdatesListener
{
    private static final String TAG = "SelectTicketsActivity";
    private static final int MAX_CHAR = 5;
    private static final String STANDARD = "Standard";
    private static final String STUDENT = "Student";
    private static final String SOLDIER = "Soldier";
    private ImageView m_imageViewMoviePic;
    private ImageView m_youtubePlayButton;
    private ImageButton m_profileWidgetImageButton;
    private TextView m_textViewMovieName;
    private TextView m_textViewMovieDate;
    private YouTubePlayerView m_youTubePlayerView;
    private YouTubePlayer.OnInitializedListener m_YouTubeInitListener;
    private YouTubePlayer m_YouTubePlayer = null;
    private boolean m_isYouTubeOnFullScreen = false;
    private TextView m_textViewStandardPrice;
    private TextView m_textViewStudentPrice;
    private TextView m_textViewSoldierPrice;
    private TextView m_textViewTotalPriceStandard;
    private TextView m_textViewTotalPriceStudent;
    private TextView m_textViewTotalPriceSoldier;
    private TextView m_textViewTotalPriceForMovie;
    private Spinner m_spinnerStandard;
    private Spinner m_spinnerStudent;
    private Spinner m_spinnerSoldier;
    private Movie m_movie;
    private UserDetails m_userDetails;
    private String m_key;
    private AnalyticsManager m_analyticsManager = AnalyticsManager.getInstance();
    private BillingManager m_BillingManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreate() >> ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tickets);

        m_BillingManager = new BillingManager(this, this);
        findViews();
        getIntentInput();

        //get m_userDetails from DB (only after getting m_userDetails we can continue to other function)
        getUserDetailsAndContinueOnCreate();

        Log.e(TAG, "onCreate() << ");
    }

    @SuppressWarnings("ConstantConditions")
    private void getUserDetailsAndContinueOnCreate()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener()
                                      {
                                          @Override
                                          public void onDataChange(DataSnapshot dataSnapshot)
                                          {
                                              m_userDetails = dataSnapshot.getValue(UserDetails.class);
                                              displayUserImage();
                                              displayMovieImage();
                                              setMovieDetails();
                                              setPrices();
                                              setSpinnersWithAdapter();
                                              initYouTubeListener();
                                              setMovieImage();
                                          }

                                          @Override
                                          public void onCancelled(DatabaseError d)
                                          {

                                          }
                                      }
        );
    }

    private void setMovieImage()
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Movie Pictures/" + m_movie.getM_thumbImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e(TAG,"pic src= "+ uri.toString());
                        Glide.with(getApplicationContext())
                                .load(uri.toString())
                                .into(m_imageViewMoviePic);
                    }
                });
    }

    private void initYouTubeListener()
    {
        m_YouTubeInitListener=new YouTubePlayer.OnInitializedListener()
        {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
            {
                youTubePlayer.loadVideo(m_movie.getM_trailerURL());
                m_YouTubePlayer = youTubePlayer;
                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener()
                {
                    @Override
                    public void onFullscreen(boolean i_isFullScreen)
                    {
                        m_isYouTubeOnFullScreen = i_isFullScreen;
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult)
            { }
        };
    }

    public void playYouTubeTrailer()
    {
        m_youTubePlayerView.initialize("AIzaSyBKg_t7VvM-LQTahdiPzn12QwYDBAnWM8Q", m_YouTubeInitListener);
    }

    private void displayMovieImage()
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Movie Pictures/" + m_movie.getM_thumbImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri i_uri)
            {
        Glide.with(getApplicationContext())
                .load(i_uri.toString())
                .into(m_imageViewMoviePic);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if(m_YouTubePlayer != null && m_isYouTubeOnFullScreen)
        {
            m_YouTubePlayer.setFullscreen(false);
            m_isYouTubeOnFullScreen = false;
        }

        else
        {
            super.onBackPressed();
        }
    }

    private void findViews()
    {
        Log.e(TAG, "findViews() >> ");
        m_youTubePlayerView=findViewById(R.id.youtubePlayer);
        m_imageViewMoviePic = findViewById(R.id.imageViewMoviePic);
        m_textViewMovieName = findViewById(R.id.textViewMovieName);
        m_textViewMovieDate = findViewById(R.id.textViewMovieDate);
        m_profileWidgetImageButton = findViewById(R.id.profile_widget);
        m_spinnerStandard = findViewById(R.id.SpinnerStandard);
        m_spinnerStudent = findViewById(R.id.SpinnerStudent);
        m_spinnerSoldier = findViewById(R.id.SpinnerSoldieer);
        m_textViewStandardPrice = findViewById(R.id.textViewStandardPrice);
        m_textViewStudentPrice = findViewById(R.id.textViewStudentPrice);
        m_textViewSoldierPrice = findViewById(R.id.textViewSoldierPrice);
        m_textViewTotalPriceForMovie = findViewById(R.id.textViewTotalPriceForMovie);
        m_textViewTotalPriceStandard = findViewById(R.id.textViewTotalPriceStandard);
        m_textViewTotalPriceStudent = findViewById(R.id.textViewTotalPriceStudent);
        m_textViewTotalPriceSoldier = findViewById(R.id.textViewTotalPriceSoldier);
        m_youtubePlayButton=findViewById(R.id.youtube_play_button);


        Log.e(TAG, "findViews() << ");
    }

    public void onClickYouTubeButton(View i_view)
    {
        m_youtubePlayButton.setVisibility(View.GONE);
        playYouTubeTrailer();
    }

    private void getIntentInput()
    {
        Log.e(TAG, "getIntentInput() >> ");

        m_movie = (Movie) getIntent().getSerializableExtra("Movie");
        m_key = getIntent().getStringExtra("Key");

        Log.e(TAG, "getIntentInput() << ");
    }

    private void setMovieDetails()
    {
        Log.e(TAG, "setMovieDetails() >> ");

        m_textViewMovieName.setText(m_movie.getM_name());
        m_textViewMovieDate.setText(m_movie.getM_date());
        //need to add pic and video

        Log.e(TAG, "setMovieDetails() << ");
    }

    private void setPrices()
    {
        Log.e(TAG, "setPrices() >> ");

        m_textViewStandardPrice.setText(limitStrToMaxChar(String.valueOf(m_movie.getM_price())));
        m_textViewStudentPrice.setText(limitStrToMaxChar(String.valueOf(m_movie.getM_price()-5)));
        m_textViewSoldierPrice.setText(limitStrToMaxChar(String.valueOf(m_movie.getM_price()-10)));

        Log.e(TAG, "setPrices() << ");

    }

    private void setSpinnersWithAdapter()
    {
        Log.e(TAG, "setSpinnersWithAdapter() >> ");

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.ArrayNumber,android.R.layout.simple_spinner_item );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_spinnerStandard.setAdapter(spinnerAdapter);
        m_spinnerStudent.setAdapter(spinnerAdapter);
        m_spinnerSoldier.setAdapter(spinnerAdapter);

        m_spinnerStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected(STANDARD,Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        m_spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected(STUDENT,Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_spinnerSoldier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected(SOLDIER,Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            { }
        });

        Log.e(TAG, "setSpinnersWithAdapter() << ");

    }

    private void spinnerItemSelected(String i_TicketType,double i_Quantity)
    {
        Log.e(TAG, "spinnerItemSelected() >> " +i_TicketType);

        TextView textView = null;
        double pricePerTicketType = 0;
        switch (i_TicketType)
        {
            case STANDARD:
                pricePerTicketType = (Double.valueOf(m_textViewStandardPrice.getText().toString()));
                textView = m_textViewTotalPriceStandard;
                break;
            case STUDENT:
                pricePerTicketType = (Double.valueOf(m_textViewStudentPrice.getText().toString()));
                textView = m_textViewTotalPriceStudent;
                break;
            case SOLDIER:
                pricePerTicketType = (Double.valueOf(m_textViewSoldierPrice.getText().toString()));
                textView = m_textViewTotalPriceSoldier;
                break;
        }

        double priceTotalTicketType = pricePerTicketType * i_Quantity;
        if(textView != null)
        {
            textView.setText(limitStrToMaxChar(String.valueOf(priceTotalTicketType)));
        }

        double totalPrice = m_spinnerStandard.getSelectedItemPosition() *
                Double.valueOf(m_textViewStandardPrice.getText().toString());
        totalPrice = totalPrice + m_spinnerStudent.getSelectedItemPosition() *
                Double.valueOf(m_textViewStudentPrice.getText().toString());
        totalPrice = totalPrice + m_spinnerSoldier.getSelectedItemPosition() *
                Double.valueOf(m_textViewSoldierPrice.getText().toString());


        m_textViewTotalPriceForMovie.setText(limitStrToMaxChar(String.valueOf(totalPrice)));

        Log.e(TAG, "spinnerItemSelected() >> " +i_TicketType);

    }

    private boolean didUserPickAnyTicket()
    {
        int totalSelectedTickets = m_spinnerStandard.getSelectedItemPosition()
                + m_spinnerSoldier.getSelectedItemPosition()
                + m_spinnerStudent.getSelectedItemPosition();

        return totalSelectedTickets != 0;
    }

    @SuppressWarnings("ConstantConditions")
    public void onClickBuyTickets(View i_view)
    {
        if(didUserPickAnyTicket())
        {
            Log.e(TAG, "onClickBuyTickets() >> ");

            if(m_userDetails.getUserName().equals("Anonymous"))
            {
                handleAnonymousOnClickBuyTickets();
            }

            else
            {
                setUserPurchaseMap();
                updateUserDetailsPurchase();
                m_analyticsManager.trackPurchase(m_movie);
                m_analyticsManager.setUserProperty(m_userDetails);

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(m_userDetails);
                String productName = m_movie.getM_name();

                String sku = BillingClient.SkuType.INAPP;
                m_BillingManager.initiatePurchaseFlow(productName, sku);

                Intent reservationSummaryIntent = new Intent(getApplicationContext(), ReservationSummaryActivity.class);
                reservationSummaryIntent.putExtra("Movie", m_movie);
                reservationSummaryIntent.putExtra("Key", m_key);
                startActivity(reservationSummaryIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                Log.e(TAG, "onClickBuyTickets() << ");
            }
        }

        else
        {
            Toast.makeText(this, "Please select number of tickets.", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUserDetailsPurchase()
    {
        m_userDetails.setM_totalPurchaseAmount(m_userDetails.getM_totalPurchaseAmount() +
                Double.valueOf(m_textViewTotalPriceForMovie.getText().toString()));
        m_userDetails.setM_totalTicketsCount(m_userDetails.getM_totalTicketsCount() +
                m_spinnerStandard.getSelectedItemPosition() +
                m_spinnerSoldier.getSelectedItemPosition() +
                m_spinnerStudent.getSelectedItemPosition());
    }

    private void setUserPurchaseMap()
    {
        Log.e(TAG, "setUserPurchaseMap() >> ");

        Map<String, Integer> purchaseMap;
        Purchase purchase = m_userDetails.getMoviesPurchaseMap().get(m_key);
        if(purchase != null)
        {
            purchase.setM_purchaseAmount(purchase.getM_purchaseAmount() +
                    Double.valueOf(m_textViewTotalPriceForMovie.getText().toString()));

            purchaseMap = purchase.getM_mapOfTypeTicketsAndQuantity();
            purchaseMap.put(STANDARD,(purchaseMap.get(STANDARD) + m_spinnerStandard.getSelectedItemPosition()));
            purchaseMap.put(SOLDIER,(purchaseMap.get(SOLDIER) + m_spinnerSoldier.getSelectedItemPosition()));
            purchaseMap.put(STUDENT,(purchaseMap.get(STUDENT) + m_spinnerStudent.getSelectedItemPosition()));
        }
        else
        {
            purchaseMap = new HashMap<String, Integer>()
            {
                {
                    put(STANDARD,m_spinnerStandard.getSelectedItemPosition());
                    put(SOLDIER,m_spinnerSoldier.getSelectedItemPosition());
                    put(STUDENT,m_spinnerStudent.getSelectedItemPosition());
                }
            };

            m_userDetails.getMoviesPurchaseMap().put(m_key,new Purchase(
                    m_key,purchaseMap,Double.valueOf(m_textViewTotalPriceForMovie.getText().toString())));
        }

        Log.e(TAG, "setUserPurchaseMap() << ");
    }

    private void handleAnonymousOnClickBuyTickets()
    {
        Intent askForSignInActivityIntent = new Intent(getApplicationContext(), AskForSignInActivity.class);
        startActivity(askForSignInActivityIntent);
    }

    private String limitStrToMaxChar(String i_Str)
    {
        String resStr = String.valueOf(i_Str);
        int maxLength = (resStr.length() < MAX_CHAR)?resStr.length():MAX_CHAR;
        return (resStr.substring(0, maxLength));
    }

    public void onClickProfileWidgetImageButton(View i_view)
    {
        ProfileWidget.onClickProfileWidget(this, m_profileWidgetImageButton, m_userDetails);
    }

    private void displayUserImage()
    {
        ProfileWidget.displayUserImage(getApplicationContext(), m_profileWidgetImageButton, m_userDetails);
    }

    @Override
    public void onBillingClientSetupFinished()
    {
        Log.e(TAG,"onBillingSetupFinished() >>");

        Log.e(TAG,"onBillingSetupFinished() <<");
    }

    @Override
    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result)
    {
        Log.e(TAG,"onConsumeFinished() >> result:"+result+" ,token:"+token);

        if(result == BillingClient.BillingResponse.OK)
        {
            Toast.makeText(getApplicationContext(), "Purchase completed successfully!", Toast.LENGTH_LONG).show();
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Purchase failed.", Toast.LENGTH_LONG).show();
        }

        Log.e(TAG,"onConsumeFinished() <<");
    }

    @Override
    public void onPurchasesUpdated(int resultCode, List<com.android.billingclient.api.Purchase> purchases)
    {
        Log.e(TAG,"onPurchasesUpdated() >> ");

        if (resultCode != BillingClient.BillingResponse.OK)
        {
            Log.e(TAG,"onPurchasesUpdated() << Error:" + resultCode);
            return;
        }

        for (com.android.billingclient.api.Purchase purchase : purchases)
        {
            Log.e(TAG, "onPurchasesUpdated() >> " + purchase.toString());

            Toast.makeText(this, "onPurchasesUpdated() >> " + purchase.getSku(), Toast.LENGTH_LONG).show();

            if (purchase.getSku().contains("credit"))
            {
                Log.e(TAG, "onPurchasesUpdated() >> consuming " + purchase.getSku());
                //Only consume  one time product (subscription can't be consumed).
                m_BillingManager.consumeAsync(purchase.getPurchaseToken());
                updateUserPurchaseOnDatabase(purchase);
            }
        }

        Log.e(TAG,"onPurchasesUpdated() <<");
    }

    private void updateUserPurchaseOnDatabase(com.android.billingclient.api.Purchase purchase)
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        MoviePurchase newPurchase = new MoviePurchase();
        newPurchase.setTime(Calendar.getInstance().getTime().toString());
        newPurchase.setSku(purchase.getSku());
        newPurchase.setOrderId(purchase.getOrderId());
        newPurchase.setToken(purchase.getPurchaseToken());
        newPurchase.setAmount(m_spinnerStandard.getSelectedItemPosition() + m_spinnerStudent.getSelectedItemPosition() + m_spinnerSoldier.getSelectedItemPosition());

        if(m_userDetails.getM_MoviesPurchases() == null)
        {
            List<MoviePurchase> moviePurchasesList = new ArrayList<>();
            moviePurchasesList.add(newPurchase);
            m_userDetails.setM_MoviesPurchases(moviePurchasesList);
        }

        else
        {
            m_userDetails.getM_MoviesPurchases().add(newPurchase);
        }

        userRef.child("MoviesPurchases").setValue(m_userDetails.getM_MoviesPurchases());
    }
}