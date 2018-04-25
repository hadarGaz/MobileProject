package com.example.hadar.exercise02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class CinemaMainActivity extends AppCompatActivity
{

    RecyclerView m_recyclerView;
    UserDetails m_userDetails;
    ImageButton m_profileMenuButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_main);

        m_profileMenuButton=findViewById(R.id.profileMenuButton);


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
