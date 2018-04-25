package com.example.hadar.exercise02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.hadar.exercise02.adapter.MoviesAdapter;
import com.example.hadar.exercise02.adapter.MovieWithKey;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class CinemaMainActivity extends AppCompatActivity
{
    private RecyclerView m_recyclerView;
    private UserDetails m_userDetails;
    private ImageButton m_profileMenuButton;
    private List<MovieWithKey> m_moviesWithKeysList;
    private MoviesAdapter m_movieAdapter;
    private DatabaseReference m_allMoviesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_main);

        findViews();
        getIntentInput();
        getAllMovies();
}

    private void getAllMovies()
    {
        m_moviesWithKeysList.clear();
        m_movieAdapter = new MoviesAdapter(m_moviesWithKeysList, m_userDetails);
        //m_recyclerView.setAdapter(m_movieAdapter); //*************************

        getAllMoviesUsingChildListeners();
    }

    private void getAllMoviesUsingChildListeners()
    {
        m_allMoviesReference = FirebaseDatabase.getInstance().getReference("Movies");
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

    }

    private void handleChildAdded(DataSnapshot i_dataSnapshot)
    {
        MovieWithKey movieWithKey = new MovieWithKey(i_dataSnapshot.getValue(Movie.class), i_dataSnapshot.getKey());
        m_moviesWithKeysList.add(movieWithKey);
        m_recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void handleChildChanged(DataSnapshot i_dataSnapshot)
    {
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
    }

    private void handleChildRemoved(DataSnapshot i_dataSnapshot)
    {
        MovieWithKey movieWithKey;

        Movie snapshotMovie = i_dataSnapshot.getValue(Movie.class);
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
    }

    private void handleChildCancelled(DatabaseError i_databaseError)
    {
        //Log.e(TAG, "onCancelled(Songs) >> " + i_databaseError.getMessage());
    }

    private void findViews()
    {
        m_profileMenuButton = findViewById(R.id.profileMenuButton);
        m_recyclerView = findViewById(R.id.my_recycler_view);
        m_moviesWithKeysList = new ArrayList<>();
    }

    private void getIntentInput()
    {
        m_userDetails = (UserDetails) getIntent().getSerializableExtra("User Details");
    }

    private void onClickProfileButton(View view)
    {
        PopupMenu popup = new PopupMenu(this, m_profileMenuButton);
        popup.getMenuInflater().inflate(R.menu.activity_user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem item)
            {
                Toast.makeText(CinemaMainActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();
    }
}