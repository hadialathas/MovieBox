package com.hadialathas.moviebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hadialathas.moviebox.R;
import com.hadialathas.moviebox.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hadialathas on 7/1/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final List<Movie> movies;
    private final int rowLayout;
    private final Context context;

    final private ListItemClickListener mOnClickListener;

    //Base url for get Image in themoviedb
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    //‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    // For most phones recommend using “w185”.
    public static final String IMAGE_BASE_SIZE = "w185";


    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context, ListItemClickListener mOnClickListener) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView imageMovie;
        final TextView textMovieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageMovie = (ImageView)itemView.findViewById(R.id.iv_movie_image);
            textMovieTitle = (TextView)itemView.findViewById(R.id.tv_movie_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, movies);
        }
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {

        holder.textMovieTitle.setText(movies.get(position).getTitle());
        Picasso.with(context)
                .load( IMAGE_BASE_URL+IMAGE_BASE_SIZE+movies.get(position).getPosterPath())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imageMovie);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex, List<Movie> ListOfMovies);
    }
}
