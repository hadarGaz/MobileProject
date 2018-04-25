package com.example.hadar.exercise02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;

public class CinemaMainActivity extends AppCompatActivity {

    RecyclerView m_recyclerView;
    UserDetails m_userDetails;
    ImageButton m_profileMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_main);
    }
}
