package com.hadialathas.moviebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hadialathas.moviebox.R;
import com.hadialathas.moviebox.model.Trailers;

import java.util.List;

/**
 * Created by hadialathas on 7/30/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private final List<Trailers> movies;
    private final int rowLayout;
    private final Context context;

    final private TrailersAdapter.ListItemClickListener mOnClickListener;

    public TrailersAdapter(List<Trailers> movies, int rowLayout, Context context, ListItemClickListener mOnClickListener) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }



    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        holder.textTrailer.setText(movies.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        final TextView textTrailer;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            textTrailer = (TextView)itemView.findViewById(R.id.tv_movie_trailers);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, movies);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, List<Trailers> ListOfTrailers);
    }
}
