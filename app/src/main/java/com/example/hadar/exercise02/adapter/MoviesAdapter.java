package com.example.hadar.exercise02.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.hadar.exercise02.Movie;
import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.UserDetails;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>
{
    private List<MovieWithKey> m_moviesWithKeysList;
    private UserDetails m_userDetails;

    public MoviesAdapter(List<MovieWithKey> i_moviesWithKeysList, UserDetails i_userDetails)
    {
        m_moviesWithKeysList = i_moviesWithKeysList;
        m_userDetails = i_userDetails;
    }

    @Override
    public int getItemCount()
    {
        return m_moviesWithKeysList.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup i_viewGroupParent, int i_viewType)
    {
        View itemView = LayoutInflater.from(i_viewGroupParent.getContext())
                .inflate(R.layout.item_movie, i_viewGroupParent, false);

        return new MovieViewHolder(i_viewGroupParent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder i_movieViewHolder, int i_position)
    {
        Movie movie = m_moviesWithKeysList.get(i_position).getMovie();
        String movieKey = m_moviesWithKeysList.get(i_position).getKey();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("thumbs/"+movie.getThumbImage());

        Glide.with(i_movieViewHolder.getContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(i_movieViewHolder.getThumbImage());
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder
    {
        private Context m_context;
        private CardView m_movieCardView;
        private ImageView m_thumbImage;
        private TextView m_nameTextView;
        private TextView m_dateTextview;
        private TextView m_genreTextView;
        private TextView m_cinemaTextView;

        public MovieViewHolder(Context i_context, View i_view)
        {
           super(i_view);
           m_context = i_context;
        }

        public Context getContext()
        {
            return m_context;
        }

        public ImageView getThumbImage()
        {
            return m_thumbImage;
        }
    }
}


