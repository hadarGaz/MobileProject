package com.example.hadar.exercise02.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.adapter.MoviesAdapter;
import com.example.hadar.exercise02.adapter.MovieWithKey;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.UserDetails;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CinemaMainActivity extends AppCompatActivity
{
    private static final String TAG = "CinemaMainActivity";

    // searchMovie,2 filter, 2 sort, pictures to movie

    private RecyclerView m_recyclerView;
    private UserDetails m_userDetails;
    private ImageView m_profileMenuButton;
    private List<MovieWithKey> m_moviesWithKeysList;
    private FirebaseUser m_firebaseUser;
    DatabaseReference m_allMoviesReference;
    RadioButton m_orderByPriceRadioButton;
    RadioButton m_orderByRatingRadioButton;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        Log.e(TAG, "onCreate() >> ");
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_cinema_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        findViews();
        getIntentInput();
        displayUserImage(); //menu button
        setRecyclerViewOptions();

        getAllMovies();
        /*
        if(m_firebaseUser != null)
            handleSignedInFirebaseUser();
        else
            getAllMovies();
            */

        Log.e(TAG, "onCreate() << ");
}

    private void getAllMovies()
    {
        Log.e(TAG, "getAllMovies() >>");

        m_moviesWithKeysList.clear();
        MoviesAdapter moviesAdapter = new MoviesAdapter(m_moviesWithKeysList, m_userDetails);
        m_recyclerView.setAdapter(moviesAdapter);

        getAllMoviesUsingChildListeners();

        Log.e(TAG, "getAllMovies() <<");
    }

    private void getAllMoviesUsingChildListeners()
    {
        Log.e(TAG, "getAllMoviesUsingChildListeners() >>");

        m_allMoviesReference = FirebaseDatabase.getInstance().getReference("Movie");

                m_allMoviesReference.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot i_dataSnapshot, String i_previousChildName)
                    {
                        handleChildAdded(i_dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot i_dataSnapshot, String i_previousChildName)
                    {
                        handleChildChanged(i_dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot i_dataSnapshot)
                    {
                        handleChildRemoved(i_dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String i_previousChildName)
                    {
                        //No Implementation needed.
                    }

                    @Override
                    public void onCancelled(DatabaseError i_databaseError)
                    {
                        handleChildCancelled(i_databaseError);
                    }
                });

        Log.e(TAG, "getAllMoviesUsingChildListeners() <<");
    }

    private void handleChildAdded(DataSnapshot i_dataSnapshot)
    {
        Log.e(TAG, "onChildAdded(Movie) >> " + i_dataSnapshot.getKey());

        Movie movie = i_dataSnapshot.getValue(Movie.class);
        MovieWithKey movieWithKey = new MovieWithKey(i_dataSnapshot.getValue(Movie.class), i_dataSnapshot.getKey());
        m_moviesWithKeysList.add(movieWithKey);
        m_recyclerView.getAdapter().notifyDataSetChanged();

        Log.e(TAG, "onChildAdded(Movie) <<");
    }

    private void handleChildChanged(DataSnapshot i_dataSnapshot)
    {
        Log.e(TAG, "onChildChanged(Movie) >> " + i_dataSnapshot.getKey());

        MovieWithKey movieWithKey;

        Movie snapshotMovie = i_dataSnapshot.getValue(Movie.class);
        String snapshotKey = i_dataSnapshot.getKey();

        for(int i = 0; i < m_moviesWithKeysList.size(); i++)
        {
            movieWithKey = m_moviesWithKeysList.get(i);

            if (movieWithKey.getKey().equals(snapshotKey))
            {
                movieWithKey.setMovie(snapshotMovie);
                m_recyclerView.getAdapter().notifyDataSetChanged();
                break;
            }
        }
        Log.e(TAG, "onChildChanged(Movie) <<");

    }

    private void handleChildRemoved(DataSnapshot i_dataSnapshot)
    {
        Log.e(TAG, "onChildRemoved(Movie) >> " + i_dataSnapshot.getKey());

        MovieWithKey movieWithKey;

        String snapshotKey = i_dataSnapshot.getKey();

        for(int i = 0; i < m_moviesWithKeysList.size(); i++)
        {
            movieWithKey = m_moviesWithKeysList.get(i);

            if (movieWithKey.getKey().equals(snapshotKey))
            {
                m_moviesWithKeysList.remove(i);
                m_recyclerView.getAdapter().notifyDataSetChanged();
                break;
            }
        }

        Log.e(TAG, "onChildRemoved(Movie) <<");
    }

    private void handleChildCancelled(DatabaseError i_databaseError)
    {
        Log.e(TAG, "onCancelled(Songs) >> " + i_databaseError.getMessage());
    }

    private void findViews()
    {
        Log.e(TAG, "findViews() >> ");

        m_profileMenuButton = findViewById(R.id.profileMenuButton);
        m_recyclerView = findViewById(R.id.movies_list);
        m_moviesWithKeysList = new ArrayList<>();
        m_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        m_orderByPriceRadioButton = findViewById(R.id.radioButtonByPrice);
        m_orderByRatingRadioButton = findViewById(R.id.radioButtonByRating);

        Log.e(TAG, "findViews() << ");
    }

    private void displayUserImage()
    {
        Log.e(TAG, "displayUserImage() >> ");

        m_profileMenuButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this).load(m_userDetails.getUserPictureUrl()).into(m_profileMenuButton);

        Log.e(TAG, "displayUserImage() << ");
    }

    private void getIntentInput()
    {
        Log.e(TAG, "getIntentInput() >> ");

        m_userDetails = (UserDetails) getIntent().getSerializableExtra("User Details");

        Log.e(TAG, "getIntentInput() << ");
    }

    public void onClickProfileButton(View i_view)
    {
        Log.e(TAG, "onClickProfileButton() >>");

        PopupMenu popup = new PopupMenu(this, m_profileMenuButton);
        popup.getMenuInflater().inflate(R.menu.activity_user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem i_item)
            {
                switch (i_item.getItemId()){
                    case R.id.viewProfile:
                        updateUIAndMoveToUserDetailsActivity();
                        break;

                    case R.id.signOut:
                        signOutAllAccounts();
                        break;
                    default:
                        break;
                }
                Toast.makeText(CinemaMainActivity.this, "You Clicked : " + i_item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();

        Log.e(TAG, "onClickProfileButton() <<");
    }

    private void updateMoviesWithKeysList(DataSnapshot i_dataSnapshot)
    {
        for (DataSnapshot dataSnapshot : i_dataSnapshot.getChildren())
        {
            Movie movie = dataSnapshot.getValue(Movie.class);

            Log.e(TAG, "updateSongList() >> adding song: " + movie.getM_name());
            String dataSnapshotKey = dataSnapshot.getKey();
            m_moviesWithKeysList.add(new MovieWithKey(movie, dataSnapshotKey));
        }

        m_recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onClickSearchButton(View i_view)
    {
        String searchString = ((EditText)findViewById(R.id.edit_text_search_movie)).getText().toString();
        String orderByMethod = ((RadioButton)findViewById(R.id.radioButtonByRating)).isChecked() ? "m_rating" : "m_price";
        Query searchMovieQuery;

        Log.e(TAG, "onSearchButtonClick() >> searchString= " + searchString + ", orderBy = " + orderByMethod);

        m_moviesWithKeysList.clear();

        if(!searchString.isEmpty())
        {
            searchMovieQuery = m_allMoviesReference.orderByChild("m_name").startAt(searchString).endAt(searchString + "\uf8ff");
        }

        else
        {
            searchMovieQuery = m_allMoviesReference.orderByChild(orderByMethod);
        }

        searchMovieQuery.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot i_snapshot)
            {
                Log.e(TAG, "onDataChange(Query) >> " + i_snapshot.getKey());

                updateMoviesWithKeysList(i_snapshot);

                Log.e(TAG, "onDataChange(Query) <<");
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "onCancelled() >>" + databaseError.getMessage());
            }

        });

        Log.e(TAG, "onSearchButtonClick() <<");
    }

    public void onClickRadioButton(View i_view)
    {
        if(i_view.getId() == R.id.radioButtonByRating)
        {
            m_orderByPriceRadioButton.setChecked(false);
        }

        else
        {
            m_orderByRatingRadioButton.setChecked(false);
        }
    }

    private void updateUIAndMoveToUserDetailsActivity()
    {
        Log.e(TAG, "updateUIAndMoveToUserDetailsActivity() >>");

        Intent userDetailsIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            userDetailsIntent.putExtra("User Details", m_userDetails);
            startActivity(userDetailsIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        Log.e(TAG, "updateUIAndMoveToUserDetailsActivity() <<");
    }

    private void setRecyclerViewOptions()
    {
        Log.e(TAG, "setRecyclerViewOptions() >>");

        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG, "setRecyclerViewOptions() <<");
    }

    private void handleSignedInFirebaseUser()
    {
        Log.e(TAG, "handleSignedInFirebaseUser() >>");

        DatabaseReference userRefernce = FirebaseDatabase.getInstance().getReference("Users/" + m_firebaseUser.getUid());
        userRefernce.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot i_dataSnapshot)
                    {
                        m_userDetails = i_dataSnapshot.getValue(UserDetails.class);
                        getAllMovies();
                    }

                    @Override
                    public void onCancelled(DatabaseError i_databaseError)
                    {
                        Log.e(TAG, "onCancelled(Users) >>" + i_databaseError.getMessage());
                    }
                });
        Log.e(TAG, "handleSignedInFirebaseUser() <<");

    }

    private void signOutAllAccounts()
    {
        signOutEmailPassAndFacebookAccount();
        signOutGoogleAccount();
    }

    private void signOutEmailPassAndFacebookAccount()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goBackToMainActivity();
    }

    private void signOutGoogleAccount()
    {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(googleSignInAccount != null)
        {
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestProfile()
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut();
            goBackToMainActivity();
        }
    }

    private void goBackToMainActivity()
    {
        Intent backToMainIntent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(backToMainIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}