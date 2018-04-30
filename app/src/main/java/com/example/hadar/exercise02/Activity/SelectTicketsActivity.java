package com.example.hadar.exercise02.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.ProfileWidget;
import com.example.hadar.exercise02.model.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SelectTicketsActivity extends AppCompatActivity {

    private static final String TAG = "SelectTicketsActivity";
    private static final int MAX_CHAR = 5;
    private ImageView m_imageViewMoviePic;
    private TextView m_textViewMovieName;
    private TextView m_textViewMovieDate;
    private YouTubePlayerView m_youTubePlayerView;
    private YouTubePlayer.OnInitializedListener m_YouTubeInitListener;
    private TextView m_textViewStandardPrice;
    private TextView m_textViewStudentPrice;
    private TextView m_textViewSoldierPrice;
    private TextView m_textViewTotalPriceStandard;
    private TextView m_textViewTotalPriceStudent;
    private TextView m_textViewTotalPriceSoldier;
    private TextView m_textViewTotalPriceForMovie;
    private Spinner m_spinnerstandard;
    private Spinner m_spinnerStudent;
    private Spinner m_spinnerSoldieer;
    private ArrayAdapter<CharSequence> m_adapter;
    private Movie m_movie;
    private UserDetails m_userDetails;
    private String m_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() >> ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tickets);

        findViews();
        getIntentInput();
        displayUserImage();
        displayMovieImage();
        setMovieDetails();
        setPrices();
        setSpinnersWithAdapter();
        initYouTubeListener();
        setMovieImage();
        Log.e(TAG, "onCreate() << ");
    }
    private void setMovieImage()
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Movie Pictures/" + m_movie.getM_thumbImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e(TAG,"pic src= "+ uri.toString());
                        Glide.with(SelectTicketsActivity.this)
                                .load(uri.toString())
                                .into(m_imageViewMoviePic);
                    }
                });
    }

    private void initYouTubeListener()
    {
        m_YouTubeInitListener=new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(m_movie.getM_trailerURL());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }

    public void playYouTubeTrailer(View view)
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
                Glide.with(getApplicationContext()).load(i_uri.toString()).into(m_imageViewMoviePic);
            }
        });
    }

    private void findViews()
    {
        Log.e(TAG, "findViews() >> ");

        m_imageViewMoviePic= (ImageView) findViewById(R.id.imageViewMoviePic);
        m_textViewMovieName= (TextView) findViewById(R.id.textViewMovieName);
        m_textViewMovieDate= (TextView) findViewById(R.id.textViewMovieDate);

        m_spinnerstandard = (Spinner)findViewById(R.id.SpinnerStandard);
        m_spinnerStudent= (Spinner)findViewById(R.id.SpinnerStudent);
        m_spinnerSoldieer= (Spinner)findViewById(R.id.SpinnerSoldieer);

        m_textViewStandardPrice= (TextView) findViewById(R.id.textViewStandardPrice);
        m_textViewStudentPrice= (TextView) findViewById(R.id.textViewStudentPrice);
        m_textViewSoldierPrice= (TextView) findViewById(R.id.textViewSoldierPrice);

        m_textViewTotalPriceForMovie = (TextView) findViewById(R.id.textViewTotalPriceForMovie);

        m_textViewTotalPriceStandard= (TextView) findViewById(R.id.textViewTotalPriceStandard);
        m_textViewTotalPriceStudent= (TextView) findViewById(R.id.textViewTotalPriceStudent);
        m_textViewTotalPriceSoldier= (TextView) findViewById(R.id.textViewTotalPriceSoldier);
        m_youTubePlayerView=findViewById(R.id.youtubePlayer);
        m_imageViewMoviePic = findViewById(R.id.imageViewMoviePic);
        m_textViewMovieName = findViewById(R.id.textViewMovieName);
        m_textViewMovieDate = findViewById(R.id.textViewMovieDate);
        m_videoViewMovieTrailer = findViewById(R.id.videoViewMovieTrailer);
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

        Log.e(TAG, "findViews() << ");
    }


    private void getIntentInput()
    {
        Log.e(TAG, "getIntentInput() >> ");

        m_movie = (Movie) getIntent().getSerializableExtra("Movie");
        m_userDetails = (UserDetails)  getIntent().getSerializableExtra("UserDetails");
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

        m_adapter = ArrayAdapter.createFromResource(this,R.array.ArrayNumber,android.R.layout.simple_spinner_item );
        m_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_spinnerStandard.setAdapter(m_adapter);
        m_spinnerStudent.setAdapter(m_adapter);
        m_spinnerSoldier.setAdapter(m_adapter);

        m_spinnerStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected("Standard",Double.valueOf(parent.getItemAtPosition(position).toString()));
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
                spinnerItemSelected("Student",Double.valueOf(parent.getItemAtPosition(position).toString()));
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
                spinnerItemSelected("Soldier",Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
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
            case "Standard":
                pricePerTicketType = (Double.valueOf(m_textViewStandardPrice.getText().toString()));
                textView = m_textViewTotalPriceStandard;
                break;
            case "Student":
                pricePerTicketType = (Double.valueOf(m_textViewStudentPrice.getText().toString()));
                textView = m_textViewTotalPriceStudent;
                break;
            case "Soldier":
                pricePerTicketType = (Double.valueOf(m_textViewSoldierPrice.getText().toString()));
                textView = m_textViewTotalPriceSoldier;
                break;
        }

        double priceTotalTicketType = pricePerTicketType * i_Quantity;
        if(textView != null)
        {
            textView.setText(limitStrToMaxChar(String.valueOf(priceTotalTicketType)));
        }

        double totalPrice = Double.valueOf(m_spinnerStandard.getSelectedItemPosition()) *
                Double.valueOf(m_textViewStandardPrice.getText().toString());
        totalPrice = totalPrice + (Double.valueOf(m_spinnerStudent.getSelectedItemPosition()) *
                Double.valueOf(m_textViewStudentPrice.getText().toString()));
        totalPrice = totalPrice + (Double.valueOf(m_spinnerSoldier.getSelectedItemPosition()) *
                Double.valueOf(m_textViewSoldierPrice.getText().toString()));


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

    public void onClickBuyTickets(View v)
    {
        if(didUserPickAnyTicket())
        {
            Log.e(TAG, "onClickBuyTickets() >> ");

            m_userDetails.getMoviesStringList().add(m_key);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(m_userDetails);


            Intent reservationSummaryIntent = new Intent(getApplicationContext(), ReservationSummaryActivity.class);
            // reservationSummaryIntent.putExtra("Key", );
            reservationSummaryIntent.putExtra("Movie", m_movie);
            reservationSummaryIntent.putExtra("Key", m_key);
            reservationSummaryIntent.putExtra("UserDetails", m_userDetails);
            startActivity(reservationSummaryIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();

            Log.e(TAG, "onClickBuyTickets() << ");
        }

        else
        {
            Toast.makeText(this, "Please select number of tickets.", Toast.LENGTH_LONG).show();
        }
    }

    private String limitStrToMaxChar(String i_Str)
    {
        String resStr = String.valueOf(i_Str);
        int maxLength = (resStr.length() < MAX_CHAR)?resStr.length():MAX_CHAR;
        return (resStr.substring(0,maxLength));
    }

    public void onClickProfileWidgetImageButton(View i_view)
    {
        ProfileWidget.onClickProfileWidget(this, m_profileWidgetImageButton, m_userDetails);
    }

    private void displayUserImage()
    {
        ProfileWidget.displayUserImage(this, m_profileWidgetImageButton, m_userDetails);
    }
}
