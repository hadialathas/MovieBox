package com.hadialathas.moviebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hadialathas.moviebox.R;
import com.hadialathas.moviebox.model.Review;


import java.util.List;

/**
 * Created by hadialathas on 7/31/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private final List<Review> reviews;
    private final int rowLayout;
    private final Context context;

    public ReviewsAdapter(List<Review> reviews, int rowLayout, Context context) {
        this.reviews = reviews;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ReviewsAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        holder.textAuthor.setText(reviews.get(position).getAuthor());
        holder.textDescription.setText(reviews.get(position).getContent());

    }


    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView textAuthor;
        final  TextView textDescription;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            textAuthor = (TextView)itemView.findViewById(R.id.tv_movie_reviews_author);
            textDescription = (TextView) itemView.findViewById(R.id.tv_movie_reviews_detail);
        }
    }


}
