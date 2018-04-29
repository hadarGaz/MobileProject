package com.example.hadar.exercise02.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.hadar.exercise02.Activity.SelectTicketsActivity;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.model.UserDetails;
import com.facebook.internal.WebDialog;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>
{

    private final String TAG = "MoviesAdapter";
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
                .child("movie Pictures/" + movie.getM_thumbImage());
        
        Glide.with(i_movieViewHolder.getContext())
            .using(new FirebaseImageLoader())
            .load(storageReference)
            .into(i_movieViewHolder.getThumbImage());

        i_movieViewHolder.setSelectedMovie(movie);
        i_movieViewHolder.setSelectedMovieKey(movieKey);
        i_movieViewHolder.getNameTextView().setText(movie.getM_name());
        i_movieViewHolder.getDateTextView().setText(movie.getM_date());
        i_movieViewHolder.getGenreTextView().setText(movie.getM_genre());
        i_movieViewHolder.getCinemaTextView().setText(movie.getM_cinemaLocation());
        i_movieViewHolder.getRating().setRating(movie.getM_rating());

        int reviewsCount = movie.getM_reviewsCount();
        if( reviewsCount > 0)
        {
            i_movieViewHolder.getReviewsCountTextView().setText("("+reviewsCount+")");
            i_movieViewHolder.getRating().setRating((float)(movie.getM_rating() / reviewsCount));
        }

        i_movieViewHolder.getMoviePriceTextView().setText(movie.getM_price() + "$");

        if(m_userDetails.getMoviesStringList() != null) {
            Iterator iterator = m_userDetails.getMoviesStringList().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(movieKey)) {
                    //i_movieViewHolder.getMoviePriceTextView().setTextColor(R.color.colorPrimary);
                    break;
                }
            }
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
    {
        private Context m_context;
        private CardView m_movieCardView;
        private ImageView m_thumbImage;
        private TextView m_nameTextView;
        private TextView m_dateTextView;
        private TextView m_genreTextView;
        private TextView m_cinemaTextView;
        private TextView m_reviewsCountTextView;
        private TextView m_moviePriceTextView;
        private Movie m_selectedMovie;
        private String m_selectedMovieKey;
        private RatingBar m_ratingBar;

        public MovieViewHolder(Context i_context, View i_view)
        {
            super(i_view);
            m_context = i_context;

            findViews(i_view);

            cardViewClickInit();
        }

        public Context getContext()
        {
            return m_context;
        }

        public ImageView getThumbImage()
        {
            return m_thumbImage;
        }

        public CardView getCardView()
        {
            return m_movieCardView;
        }

        public TextView getNameTextView()
        {
            return m_nameTextView;
        }

        public TextView getDateTextView()
        {
            return m_dateTextView;
        }

        public TextView getGenreTextView()
        {
            return m_genreTextView;
        }

        public TextView getCinemaTextView()
        {
            return m_cinemaTextView;
        }

        public TextView getReviewsCountTextView()
        {
            return m_reviewsCountTextView;
        }

        public TextView getMoviePriceTextView()
        {
            return m_moviePriceTextView;
        }

        public RatingBar getRating()
        {
            return m_ratingBar;
        }

        public void setThumbImage(ImageView i_thumbImage)
        {
            m_thumbImage = i_thumbImage;
        }

        public void setSelectedMovie(Movie i_movie)
        {
            m_selectedMovie = i_movie;
        }

        public void setSelectedMovieKey(String i_key)
        {
            m_selectedMovieKey = i_key;
        }

        private void findViews(View i_view)
        {
            m_movieCardView = i_view.findViewById(R.id.card_view_movie);
            m_thumbImage = i_view.findViewById(R.id.movie_thumb_image);
            m_nameTextView = i_view.findViewById(R.id.movie_name);
            m_dateTextView = i_view.findViewById(R.id.movie_date);
            m_genreTextView = i_view.findViewById(R.id.movie_genre);
            m_cinemaTextView = i_view.findViewById(R.id.movie_cinema);
            m_reviewsCountTextView = i_view.findViewById(R.id.movie_review_count);
            m_ratingBar = i_view.findViewById(R.id.movie_rating);
            m_moviePriceTextView = i_view.findViewById(R.id.movie_price);
        }

        private void cardViewClickInit()
        {
            m_movieCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View i_view)
                {
                    Context context = i_view.getContext();
                    Intent intent = new Intent(context, SelectTicketsActivity.class);
                    intent.putExtra("Movie",m_selectedMovie);
                    intent.putExtra("Key", m_selectedMovieKey);
                    intent.putExtra("UserDetails", m_userDetails);
                    context.startActivity(intent);

                    //IMPLEMENT MISSING GO TO RIGHT ACTIVITY ON CHOOSING MOVIE *********************
                }
            });
        }
    }
}


