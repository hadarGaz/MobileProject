package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.hadar.exercise02.adapter.MoviesAdapter;
import com.example.hadar.exercise02.model.MovieWithKey;
import com.example.hadar.exercise02.model.Movie;
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
import com.google.firebase.database.ValueEventListener;

public class CinemaMainActivity extends AppCompatActivity
{
    private RecyclerView m_recyclerView;
    private UserDetails m_userDetails;
    private ImageView m_profileMenuButton;
    private List<MovieWithKey> m_moviesWithKeysList;
    private FirebaseUser m_firebaseUser;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle i_savedInstanceState)
    {
        super.onCreate(i_savedInstanceState);
        setContentView(R.layout.activity_cinema_main);

        findViews();
        displayUserImage();
        setRecyclerViewOptions();
        getIntentInput();

        if(m_firebaseUser != null)
            handleSignedInFirebaseUser();
        else
            getAllMovies();
}

    private void getAllMovies()
    {
        m_moviesWithKeysList.clear();
        MoviesAdapter moviesAdapter = new MoviesAdapter(m_moviesWithKeysList, m_userDetails);
        m_recyclerView.setAdapter(moviesAdapter);

        getAllMoviesUsingChildListeners();
    }

    private void getAllMoviesUsingChildListeners()
    {
        DatabaseReference allMoviesReference = FirebaseDatabase.getInstance().getReference("Movies");
                allMoviesReference.addChildEventListener(new ChildEventListener()
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
        Log.e(TAG, "onCancelled(Songs) >> " + i_databaseError.getMessage());
    }

    private void findViews()
    {
        m_profileMenuButton = findViewById(R.id.profileMenuButton);
        m_recyclerView = findViewById(R.id.my_recycler_view);
        m_moviesWithKeysList = new ArrayList<>();
        m_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void displayUserImage()
    {
        m_profileMenuButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this).load(m_userDetails.getUserPictureUrl()).into(m_profileMenuButton);
    }

    private void getIntentInput()
    {
        m_userDetails = (UserDetails) getIntent().getSerializableExtra("User Details");
    }

    public void onClickProfileButton(View view)
    {
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

                        break;
                    default:
                        break;
                }
                Toast.makeText(CinemaMainActivity.this, "You Clicked : " + i_item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();
    }

    private void updateUIAndMoveToUserDetailsActivity()
    {
            Intent userDetailsIntent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            userDetailsIntent.putExtra("User Details", m_userDetails);
            startActivity(userDetailsIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setRecyclerViewOptions()
    {
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void handleSignedInFirebaseUser()
    {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users/" + m_firebaseUser.getUid());
                userReference.addValueEventListener(new ValueEventListener()
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
    }
}