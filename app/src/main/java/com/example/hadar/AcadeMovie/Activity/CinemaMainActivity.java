package com.example.hadar.AcadeMovie.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

import com.example.hadar.AcadeMovie.Analytics.AnalyticsManager;
import com.example.hadar.AcadeMovie.R;
import com.example.hadar.AcadeMovie.adapter.MoviesAdapter;
import com.example.hadar.AcadeMovie.adapter.MovieWithKey;
import com.example.hadar.AcadeMovie.model.GifPlayer;
import com.example.hadar.AcadeMovie.model.Movie;
import com.example.hadar.AcadeMovie.model.ProfileWidget;
import com.example.hadar.AcadeMovie.model.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private AnalyticsManager m_analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        Log.e(TAG, "onCreate() >> ");

        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_cinema_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        findViews();
        setSortBySpinnerValues();
        setRecyclerViewOptions();

        getUserDetailsAndContinueOnCreate();

        GifPlayer.setCinemaAnim(true);
        Log.e(TAG, "gif source= "+ GifPlayer.s_LoadingBar.getId());
        GifPlayer.playGif();

        Log.e(TAG, "onCreate() << ");
}

    @Override
    public void onBackPressed()
    {
        showExitAppDialog();
    }

    private void showExitAppDialog()
    {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface i_dialogInterface, int i_num)
                    {
                        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
                        exitIntent.addCategory(Intent.CATEGORY_HOME);
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(exitIntent);
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    private void getUserDetailsAndContinueOnCreate()
    {
        Log.e(TAG, "getUserDetailsAndContinueOnCreate() >> ");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener()
                                      {
                                          @Override
                                          public void onDataChange (DataSnapshot dataSnapshot) {
                                              m_userDetails = dataSnapshot.getValue(UserDetails.class);
                                              displayUserImage();
                                              getAllMovies();
                                              onSortBySpinnerItemSelection();
                                          }
                                          @Override
                                          public void onCancelled (DatabaseError d) {

                                          }
                                      }
        );

        Log.e(TAG, "getUserDetailsAndContinueOnCreate() << ");
    }

    private void setSortBySpinnerValues()
    {
        Log.e(TAG, "setSortBySpinnerValues() >> ");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.sort_spinner_item, sortingMethods);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        m_sortBySpinner.setAdapter(spinnerAdapter);

        Log.e(TAG, "setSortBySpinnerValues() << ");

    }

    private void onSortBySpinnerItemSelection()
    {
        Log.e(TAG, "onSortBySpinnerItemSelection() >> ");

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

        Log.e(TAG, "onSortBySpinnerItemSelection() << ");

    }

    private void sortBySpinnerItemSelected(String i_sortingMethod)
    {
        Log.e(TAG, "sortBySpinnerItemSelected() >> ");

        if(i_sortingMethod.equals("Rating"))
        {
            sortMoviesByRating();
        }

        else if (i_sortingMethod.equals("Genre"))
        {
            sortMoviesByGenre();
        }

        m_recyclerView.getAdapter().notifyDataSetChanged();

        Log.e(TAG, "sortBySpinnerItemSelected() << ");

    }

    private void sortMoviesByRating()
    {
        Log.e(TAG, "sortMoviesByRating() >> ");

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

        Log.e(TAG, "sortMoviesByRating() << ");

    }

    private void sortMoviesByGenre()
    {
        Log.e(TAG, "sortMoviesByGenre() >> ");

        m_moviesWithKeysList.sort(new Comparator<MovieWithKey>()
        {
            @Override
            public int compare(MovieWithKey i_movieWithKey1, MovieWithKey i_MovieWithKey2)
            {
                return i_movieWithKey1.getMovie().getM_genre().compareTo(i_MovieWithKey2.getMovie().getM_genre());
            }
        });

        Log.e(TAG, "sortMoviesByGenre() << ");

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
        Log.e(TAG, "displayUserImage() >> ");

        ProfileWidget.displayUserImage(getApplicationContext(), m_profileWidgetImageButton, m_userDetails);

        Log.e(TAG, "displayUserImage() << ");

    }

    private void updateMoviesWithKeysList(DataSnapshot i_dataSnapshot)
    {
        Log.e(TAG, "updateMoviesWithKeysList() >> ");

        for (DataSnapshot dataSnapshot : i_dataSnapshot.getChildren())
        {
            Movie movie = dataSnapshot.getValue(Movie.class);

            Log.e(TAG, "updateSongList() >> adding song");
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

        Log.e(TAG, "updateMoviesWithKeysList() << ");

    }

    public void onClickSearchButton(View i_view)
    {

        String searchString = ((EditText)findViewById(R.id.edit_text_search_movie)).getText().toString();
        String orderByMethod = ((RadioButton)findViewById(R.id.radioButtonByRating)).isChecked() ? "m_averageRating" : "m_price";
        Query searchMovieQuery;

        Log.e(TAG, "onSearchButtonClick() >> searchString= " + searchString + ", orderBy = " + orderByMethod);

        m_moviesWithKeysList.clear();

        if(!searchString.isEmpty())
        {
            searchMovieQuery = m_allMoviesReference.orderByChild("m_name").startAt(searchString).endAt(searchString + "\uf8ff");
            m_analyticsManager.trackSearchEvent(searchString);
            m_analyticsManager.setUserProperty(m_userDetails);
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
        Log.e(TAG, "onClickRadioButton() >> ");

        if(i_view.getId() == R.id.radioButtonByRating)
        {
            m_orderByPriceRadioButton.setChecked(false);
        }

        else
        {
            m_orderByRatingRadioButton.setChecked(false);
        }

        Log.e(TAG, "onClickRadioButton() << ");

    }

    public void onClickProfileWidgetImageButton(View i_view)
    {
        Log.e(TAG, "onClickProfileWidgetImageButton() >> ");

        ProfileWidget.onClickProfileWidget(this, m_profileWidgetImageButton, m_userDetails);

        Log.e(TAG, "onClickProfileWidgetImageButton() >> ");
    }
}