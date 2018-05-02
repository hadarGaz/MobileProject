package com.example.hadar.exercise02.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import android.widget.ImageButton;

import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.adapter.MoviesAdapter;
import com.example.hadar.exercise02.adapter.MovieWithKey;
import com.example.hadar.exercise02.model.GifPlayer;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.ProfileWidget;
import com.example.hadar.exercise02.model.UserDetails;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// searchMovie,2 filter, 2 sort, pictures to movie
public class CinemaMainActivity extends AppCompatActivity
{
    private static final String TAG = "CinemaMainActivity";
    private final String[] sortingMethods = {"", "Rating", "Genre"};
    private RecyclerView m_recyclerView;
    private UserDetails m_userDetails;
    private ImageButton m_profileWidgetImageButton;
    private List<MovieWithKey> m_moviesWithKeysList;
    private DatabaseReference m_allMoviesReference;
    private RadioButton m_orderByPriceRadioButton;
    private RadioButton m_orderByRatingRadioButton;
    private Spinner m_sortBySpinner;

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        Log.e(TAG, "onCreate() >> ");

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_cinema_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        findViews();
        setSortBySpinnerValues();
        getIntentInput();
        displayUserImage();
        setRecyclerViewOptions();
        getAllMovies();
        onSortBySpinnerItemSelection();

        GifPlayer.setCinemaAnim(true);
        Log.e(TAG, "gif source= "+ GifPlayer.s_LoadingBar.getId());
        GifPlayer.playGif();

        Log.e(TAG, "onCreate() << ");
}

    private void setSortBySpinnerValues()
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.sort_spinner_item, sortingMethods);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        m_sortBySpinner.setAdapter(spinnerAdapter);
    }

    private void onSortBySpinnerItemSelection()
    {
        m_sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> i_parent, View i_view, int i_position, long i_id)
            {
                sortBySpinnerItemSelected(i_parent.getItemAtPosition(i_position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                //No implementation needed
            }
        });
    }

    private void sortBySpinnerItemSelected(String i_sortingMethod)
    {
        if(i_sortingMethod == "Rating")
        {
            sortMoviesByRating();
        }

        else if (i_sortingMethod == "Genre")
        {
            sortMoviesByGenre();
        }

        m_recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void sortMoviesByRating()
    {
        //Ascending sort of rating
        m_moviesWithKeysList.sort(new Comparator<MovieWithKey>()
        {
            @Override
            public int compare(MovieWithKey i_movieWithKey1, MovieWithKey i_movieWithKey2)
            {
                //replace 1 and 2 if we wish to have descending sort
                return i_movieWithKey2.getMovie().compareRating(i_movieWithKey1.getMovie());
            }
        });
    }

    private void sortMoviesByGenre()
    {
        m_moviesWithKeysList.sort(new Comparator<MovieWithKey>()
        {
            @Override
            public int compare(MovieWithKey i_movieWithKey1, MovieWithKey i_MovieWithKey2)
            {
                return i_movieWithKey1.getMovie().getM_genre().compareTo(i_MovieWithKey2.getMovie().getM_genre());
            }
        });
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
        //Log.e(TAG, "onChildAdded(Movie) >> " + i_dataSnapshot.getKey());

        Movie movie = i_dataSnapshot.getValue(Movie.class);
        MovieWithKey movieWithKey = new MovieWithKey(i_dataSnapshot.getValue(Movie.class), i_dataSnapshot.getKey());
        m_moviesWithKeysList.add(movieWithKey);
        m_recyclerView.getAdapter().notifyDataSetChanged();

        //Log.e(TAG, "onChildAdded(Movie) <<");
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

        m_profileWidgetImageButton = findViewById(R.id.profile_widget);
        m_recyclerView = findViewById(R.id.movies_list);
        m_moviesWithKeysList = new ArrayList<>();
        m_orderByPriceRadioButton = findViewById(R.id.radioButtonByPrice);
        m_orderByRatingRadioButton = findViewById(R.id.radioButtonByRating);
        m_sortBySpinner = findViewById(R.id.sort_by_spinner);
        GifPlayer.s_LoadingBar =findViewById(R.id.CinemaBar);


        Log.e(TAG, "findViews() << ");
    }

    private void setRecyclerViewOptions()
    {
        Log.e(TAG, "setRecyclerViewOptions() >>");

        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG, "setRecyclerViewOptions() <<");
    }

    private void displayUserImage()
    {
        ProfileWidget.displayUserImage(this, m_profileWidgetImageButton, m_userDetails);
    }

    private void getIntentInput()
    {
        Log.e(TAG, "getIntentInput() >> ");

        m_userDetails = (UserDetails) getIntent().getSerializableExtra("User Details");

        Log.e(TAG, "getIntentInput() << ");
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

        m_moviesWithKeysList.sort(new Comparator<MovieWithKey>()
        {
            @Override
            public int compare(MovieWithKey i_movieWithKey1, MovieWithKey i_MovieWithKey2)
            {
                return 0;
            }
        });

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

    public void onClickProfileWidgetImageButton(View i_view)
    {
        ProfileWidget.onClickProfileWidget(this, m_profileWidgetImageButton, m_userDetails);
    }
}