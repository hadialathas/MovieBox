package com.hadialathas.moviebox.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hadialathas.moviebox.R;
import com.hadialathas.moviebox.adapter.MoviesAdapter;
import com.hadialathas.moviebox.adapter.ReviewsAdapter;
import com.hadialathas.moviebox.adapter.TrailersAdapter;
import com.hadialathas.moviebox.model.Review;
import com.hadialathas.moviebox.model.ReviewsResponse;
import com.hadialathas.moviebox.model.Trailers;
import com.hadialathas.moviebox.model.TrailersResponse;
import com.hadialathas.moviebox.rest.ApiClient;
import com.hadialathas.moviebox.rest.ApiInterface;
import com.hadialathas.moviebox.data.MovieContract.MovieEntries;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.ListItemClickListener {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private String title, backgroundImageURL, posterImageURL, releaseDate, voteAvg,
            voteCount, popularity, synopsis, id;

    private boolean isFavorite;

    List<Trailers> trailers;

    //Using ButterKnife for Automatically finds each field by the specified ID.
    @BindView(R.id.tv_movie_title) TextView mLblTitle;
    @BindView(R.id.tv_movie_release_date) TextView mLblReleaseDate;
    @BindView(R.id.tv_movie_vote_avg) TextView mLblVoteAvg;
    @BindView(R.id.tv_movie_vote_total) TextView mLblVoteCount;
    @BindView(R.id.tv_movie_popularity_rank) TextView mLblPopularity;
    @BindView(R.id.tv_movie_synopsis_details) TextView mLblSynopsis;

    @BindView(R.id.iv_movie_background) ImageView mBackgroundImage;
    @BindView(R.id.iv_movie_image) ImageView mPosterImage;
    @BindView(R.id.iv_favorites) ImageView mFavoritesImage;

    @BindView(R.id.rv_trailers)
    RecyclerView rvMovieTrailer;

    @BindView(R.id.rv_reviews)
    RecyclerView rvMovieReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Get movie details data from Intent
        getMovieDetails();

        //Set up the layout
        ButterKnife.bind(this);
        initView();

        //get Trailers from Movie based on Movie ID
        getMovieTrailers(Integer.parseInt(id));

        //get Reviews from Movie based on Movie ID
        getMovieReviews(Integer.parseInt(id));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.action_share) {
            shareYoutubeVideo(title, trailers);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A method for get data from MainActivity. This method will get intent  and
     * use that String from intent to display the appropriate text for each movie details.
     */
    private void getMovieDetails()
    {
        Intent intent = getIntent();

        if(intent.hasExtra(MainActivity.KEY_TITLE))
        {
            title = intent.getStringExtra(MainActivity.KEY_TITLE);
        }

        if (intent.hasExtra(MainActivity.KEY_RELEASE_DATE))
        {
            releaseDate = intent.getStringExtra(MainActivity.KEY_RELEASE_DATE);
        }

        if(intent.hasExtra(MainActivity.KEY_BACKGROUND_IMAGE))
        {
            backgroundImageURL =
                    intent.getStringExtra(MainActivity.KEY_BACKGROUND_IMAGE);
        }

        if(intent.hasExtra(MainActivity.KEY_POSTER_IMAGE))
        {
            posterImageURL =
                    intent.getStringExtra(MainActivity.KEY_POSTER_IMAGE);
        }

        if(intent.hasExtra(MainActivity.KEY_VOTE_AVG))
        {
            voteAvg = intent.getStringExtra(MainActivity.KEY_VOTE_AVG);
        }

        if(intent.hasExtra(MainActivity.KEY_VOTE_COUNT))
        {
            voteCount = intent.getStringExtra(MainActivity.KEY_VOTE_COUNT);
        }

        if(intent.hasExtra(MainActivity.KEY_POPULARITY))
        {
            popularity = intent.getStringExtra(MainActivity.KEY_POPULARITY);
        }

        if(intent.hasExtra(MainActivity.KEY_SYNOPSIS))
        {
            synopsis = intent.getStringExtra(MainActivity.KEY_SYNOPSIS);
        }

        if(intent.hasExtra(MainActivity.KEY_ID))
        {
            id = intent.getStringExtra(MainActivity.KEY_ID);
        }

        if(intent.hasExtra(MainActivity.KEY_FAVORITES))
        {
            isFavorite = intent.getBooleanExtra(MainActivity.KEY_FAVORITES, true);
        }
    }

    /**
     * A method for initialize all elements in the layout and set data from intent to each element
     * to display the appropriate text for each movie details.
     */
    private void initView()
    {
        //Set text of textView with data from getIntent()
        mLblTitle.setText(title);
        mLblReleaseDate.setText(releaseDate);
        mLblVoteAvg.setText(voteAvg);
        mLblVoteCount.setText(voteCount);
        mLblPopularity.setText(popularity);
        mLblSynopsis.setText(synopsis);


        //Set source of imageView with URL from getIntent()
        Picasso.with(this)
                .load(MoviesAdapter.IMAGE_BASE_URL + MoviesAdapter.IMAGE_BASE_SIZE + backgroundImageURL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher).into(mBackgroundImage);
        Picasso.with(this)
                .load(MoviesAdapter.IMAGE_BASE_URL + MoviesAdapter.IMAGE_BASE_SIZE + posterImageURL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher).into(mPosterImage);

        rvMovieTrailer.setHasFixedSize(false);
        rvMovieTrailer.setLayoutManager(new LinearLayoutManager(this));

        rvMovieReviews.setHasFixedSize(false);
        rvMovieReviews.setLayoutManager(new LinearLayoutManager(this));

        //If movie is favorite change the default icon
        if (isFavorite)
        {
            mFavoritesImage.setImageResource(R.drawable.star);
        }

        mFavoritesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isFavorite) {
                    // Create new empty ContentValues object
                    ContentValues contentValues = new ContentValues();

                    // Put the task description and selected mPriority into the ContentValues
                    contentValues.put(MovieEntries.COLUMN_MOVIE_ID, id);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_TITLE, title);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_BACKGROUND_IMAGE, backgroundImageURL);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_POSTER_IMAGE, posterImageURL);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_VOTE_AVG, voteAvg);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_VOTE_COUNT, voteCount);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_POPULARITY, popularity);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_SYNOPSIS, synopsis);
                    contentValues.put(MovieEntries.COLUMN_MOVIE_IS_FAVORITE, "true");

                    // Insert the content values via a ContentResolver
                    Uri uri = getContentResolver().insert(MovieEntries.CONTENT_URI, contentValues);

                    if (uri != null) {
                        Toast.makeText(getBaseContext(), R.string.add_to_favorites, Toast.LENGTH_LONG).show();
                        mFavoritesImage.setImageResource(R.drawable.star);
                    }
                }
                else {

                    //Set the name of column and value which want to delete
                    String whereClause =  MovieEntries.COLUMN_MOVIE_ID + " =?";
                    String[] whereArgs = { id };

                    int removeFavoriteMovie = getContentResolver()
                            .delete(MovieEntries.CONTENT_URI,whereClause, whereArgs);

                    //if delete success, show message
                    if(removeFavoriteMovie > 0){
                        Toast.makeText(getBaseContext(), R.string.remove_to_favorites, Toast.LENGTH_LONG).show();
                        mFavoritesImage.setImageResource(R.drawable.star_unselected);
                    }

                }
            }
        });


    }

    /**
     * A method for get data from API. This method will call the API Trailers Movies
     * and when success will return the list of trailers.
     */
    private void getMovieTrailers(int movieId){

        if (isOnline()){
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<TrailersResponse> call;

            call = apiService.getMovieVideos(movieId, MainActivity.API_KEY);

            call.enqueue(new Callback<TrailersResponse>() {
                @Override
                public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                    trailers = response.body().getResults();
                    rvMovieTrailer.setAdapter(new TrailersAdapter(trailers, R.layout.trailers_item,
                            getApplicationContext(), DetailsActivity.this));

                }

                @Override
                public void onFailure(Call<TrailersResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        }

    }

    /**
     * A method for get data from API. This method will call the API Reviews
     * and when success will return the list of reviews.
     */
    private void getMovieReviews(int movieId) {

        if (isOnline()) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ReviewsResponse> call;

            call = apiService.getMovieReviews(movieId, MainActivity.API_KEY);

            call.enqueue(new Callback<ReviewsResponse>() {
                @Override
                public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                    List<Review> reviews = response.body().getResults();
                    rvMovieReviews.setAdapter(new ReviewsAdapter(reviews, R.layout.reviews_item,
                            getApplicationContext()));

                }

                @Override
                public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });

        }
    }

    /**
     * A method to check network access in the phone
     * if phone have internet connection, will return true.
     */
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onListItemClick(int position, List<Trailers> ListOfTrailers) {

        //get Selected Trailers
        Trailers trailers = ListOfTrailers.get(position);

        String url = trailers.getKey();

        watchYoutubeVideo(url);

    }

    /**
     * A method to open url video
     * and go to youtube website.
     */
    private void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    /**
     * A method to share url first trailer of the movie
     * Will open pop-up intent to social media
     */
    private void shareYoutubeVideo(String title, List<Trailers> ListOfTrailers){

        if(ListOfTrailers != null) {
            Trailers trailers = ListOfTrailers.get(0);
            String url = trailers.getKey();

            Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + url);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, title + " : " + uri.toString());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        }
    }

}
