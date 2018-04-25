package com.example.hadar.exercise02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.bumptech.glide.Glide;


public class CinemaMainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private RecyclerView m_recyclerView;
    private UserDetails m_userDetails;
    private ImageView m_profileMenuButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_main);
        Log.w(TAG,"onCreate>>");

        m_userDetails=(UserDetails) getIntent().getSerializableExtra("User Details");
        m_profileMenuButton=findViewById(R.id.test);
        displayUserImage();
        Log.w(TAG,"onCreate<<");
    }

    private void displayUserImage()
    {
        m_profileMenuButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this).load(m_userDetails.getUserPictureUrl()).into(m_profileMenuButton);
    }

    protected void onClickProfileButton(View i_view)
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


}
