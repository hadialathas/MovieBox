package com.hadialathas.moviebox.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import com.hadialathas.moviebox.R;
import com.hadialathas.moviebox.adapter.MoviesAdapter;
import com.hadialathas.moviebox.data.MovieContract;
import com.hadialathas.moviebox.model.Movie;
import com.hadialathas.moviebox.model.MoviesResponse;
import com.hadialathas.moviebox.rest.ApiClient;
import com.hadialathas.moviebox.rest.ApiInterface;
import com.hadialathas.moviebox.data.MovieContract.MovieEntries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener,
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Api Key themoviedb.org
    public final static String API_KEY = "f96e0fb49215019131b6f152c14ec278";

    //Key for intent put extra
    public static final String KEY_TITLE = "title";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_BACKGROUND_IMAGE = "background_image";
    public static final String KEY_POSTER_IMAGE = "poster_image";
    public static final String KEY_VOTE_AVG = "vote_avg";
    public static final String KEY_VOTE_COUNT = "vote_count";
    public static final String KEY_POPULARITY = "popularity";
    public static final String KEY_SYNOPSIS= "synopsis";
    public static final String KEY_ID= "id";
    public static final String KEY_FAVORITES= "is_favorites";

    //Element for sort the movie
    private int typeSort = 0;
    public static final String SORT_MOVIE = "sorting";

    //Using ButterKnife for Automatically finds each field by the specified ID.
    @BindView(R.id.recyclerview_movie) RecyclerView recyclerViewMovie;
    @BindView(R.id.layout_content) LinearLayout mLayoutContent;
    @BindView(R.id.layout_error) LinearLayout mLayoutError;
    @BindView(R.id.tv_title) TextView mLblTitle;
    @BindView(R.id.btn_refresh) Button mBtnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mBtnRefresh.setOnClickListener(this);

        //Set up RecyclerView
        int numberOfColumns = numberOfColumns();
        recyclerViewMovie.setHasFixedSize(false);
        recyclerViewMovie.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        if (savedInstanceState == null) {
            //Get the data from API
            getMovieData();
        } else {

            typeSort = savedInstanceState.getInt(SORT_MOVIE);
            //Get the data from API
            getMovieData();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SORT_MOVIE, typeSort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //When user click on the sort icon in the top of right screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.action_sort)
        {
            //Set up pop-up dialog for sort the list of movies
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_dialog_sort_movie);
            dialog.setTitle(R.string.sort_by);
            dialog.setCancelable(true);

            final RadioButton mRadioSortPopular = (RadioButton) dialog.findViewById(R.id.rb_sort_popular);
            final RadioButton mRadioSortTopRated = (RadioButton) dialog.findViewById(R.id.rb_sort_topRated);
            final RadioButton mRadioSortFavorite = (RadioButton) dialog.findViewById(R.id.rb_sort_favorite);

            if(typeSort == 0) {
                mRadioSortPopular.setChecked(true);
                mRadioSortTopRated.setChecked(false);
                mRadioSortFavorite.setChecked(false);
            }
            else if(typeSort == 1){
                mRadioSortPopular.setChecked(false);
                mRadioSortTopRated.setChecked(true);
                mRadioSortFavorite.setChecked(false);
            }
            else {
                mRadioSortPopular.setChecked(false);
                mRadioSortTopRated.setChecked(false);
                mRadioSortFavorite.setChecked(true);
            }


            mRadioSortPopular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeSort = 0;
                    getMovieData();
                    mRadioSortPopular.setChecked(true);
                    mRadioSortTopRated.setChecked(false);
                    mRadioSortFavorite.setChecked(false);
                    dialog.dismiss();
                }
            });

            mRadioSortTopRated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeSort = 1;
                    getMovieData();
                    mRadioSortPopular.setChecked(false);
                    mRadioSortTopRated.setChecked(true);
                    mRadioSortFavorite.setChecked(false);
                    dialog.dismiss();
                }
            });

            mRadioSortFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeSort = 2;
                    getMovieData();
                    mLblTitle.setText(R.string.favorites);
                    mRadioSortPopular.setChecked(false);
                    mRadioSortTopRated.setChecked(false);
                    mRadioSortFavorite.setChecked(true);
                    dialog.dismiss();
                }
            });

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //When user click on the list of popular or top rated movies
    @Override
    public void onListItemClick(int position, List<Movie> listOfMovies) {

        //Get Selected movie
        Movie movies = listOfMovies.get(position);

        Intent intent = new Intent(this, DetailsActivity.class);

        //Pass movie details to DetailsActivity.class contains title, release date, movie poster, vote average, and plot synopsis.
        intent.putExtra(KEY_TITLE, movies.getTitle());
        intent.putExtra(KEY_RELEASE_DATE, movies.getReleaseDate());
        intent.putExtra(KEY_BACKGROUND_IMAGE, movies.getBackdropPath());
        intent.putExtra(KEY_POSTER_IMAGE, movies.getPosterPath());
        intent.putExtra(KEY_VOTE_AVG, movies.getVoteAverage().toString());
        intent.putExtra(KEY_VOTE_COUNT, movies.getVoteCount().toString());
        intent.putExtra(KEY_POPULARITY,  String.format(Locale.getDefault(), "%.2f", movies.getPopularity()));
        intent.putExtra(KEY_SYNOPSIS, movies.getOverview());
        intent.putExtra(KEY_ID, movies.getId().toString());

        //Pass paramater favorites, is the movie has been selected in favorites list or not.
        if(checkIfDataIsFavorite(movies.getId().toString())){
            intent.putExtra(KEY_FAVORITES, true);
        }
        else {
            intent.putExtra(KEY_FAVORITES, false);
        }

        //Show a DetailsActivity.Class when an item is clicked, displaying the movie details
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {

        //When user don't have network connection, and click on the refresh button
        if(v.getId() == R.id.btn_refresh)
        {
            getMovieData();
        }

    }


    /**
     * A method to get MovieData from API or ContentProvider
     * and display it based on the user choose
     */
    private void getMovieData()
    {
        if(isOnline()) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MoviesResponse> call = null;

            //check if want to call API to get Popular Movies or Top Rated Movies
            if(typeSort == 0) {
                mLblTitle.setText(R.string.most_popular);
                call = apiService.getPopularMovies(API_KEY);
                getApiMovie (call);
            }
            else if(typeSort == 1) {
                mLblTitle.setText(R.string.top_rated);
                call = apiService.getTopRatedMovies(API_KEY);
                getApiMovie (call);
            }
            else {
                mLblTitle.setText(R.string.favorites);
               getFavoriteMovie();
            }

        }
        else {

            //Set the visibility of layout when failed to get the list of movies
            mLayoutError.setVisibility(View.VISIBLE);
            mLayoutContent.setVisibility(View.INVISIBLE);

        }
    }

    /**
     * A method for get data from API. This method will call the API Popular Movies or Top Rated Movies
     * and when success will return the list of movies.
     */
    private void getApiMovie(Call<MoviesResponse> call)
    {
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                List<Movie> movies = response.body().getResults();
                recyclerViewMovie.setAdapter(new MoviesAdapter(movies, R.layout.movie_item, getApplicationContext(), MainActivity.this));

                //Set the visibility of layout when success to get the list of movies
                mLayoutError.setVisibility(View.INVISIBLE);
                mLayoutContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    /**
     * A method for get data favorite movie from Cursor and save the cursor into list.
     * and set the list to adapter
     */
    private void getFavoriteMovie()
    {
        Cursor mCursor = getContentResolver().query(
                MovieContract.MovieEntries.CONTENT_URI,  // The content URI of the words table
                null,                       // The columns to return for each row
                null,                   // Either null, or the word the user entered
                null,                    // Either empty, or the string the user entered
                MovieContract.MovieEntries.COLUMN_MOVIE_ID);

        List<Movie> movies = saveCursorToList(mCursor);

        recyclerViewMovie.setAdapter(new MoviesAdapter(movies, R.layout.movie_item, getApplicationContext(), MainActivity.this));

        //Set the visibility of layout when success to get the list of movies
        mLayoutError.setVisibility(View.INVISIBLE);
        mLayoutContent.setVisibility(View.VISIBLE);
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

    /**
     * A method to dynamically calculate the number of columns
     * and the layout would adapt to the screen size and orientation
     * will return the number of columns.
     */
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 500;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    /**
     * A method to save all cursor data
     * and return the data into list<Movie>
     */
    private List<Movie> saveCursorToList(Cursor mCursor){
        List<Movie> movies = new ArrayList<>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            Movie movieItem = new Movie();
            movieItem.setId(mCursor
                    .getInt(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_ID)));
            movieItem.setTitle(mCursor
                    .getString(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_TITLE)));
            movieItem.setReleaseDate(mCursor
                    .getString(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_RELEASE_DATE)));
            movieItem.setPosterPath(mCursor
                    .getString(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_POSTER_IMAGE)));
            movieItem.setBackdropPath(mCursor
                    .getString(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_BACKGROUND_IMAGE)));
            movieItem.setVoteAverage(mCursor
                    .getDouble(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_VOTE_AVG)));
            movieItem.setVoteCount(mCursor
                    .getInt(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_VOTE_COUNT)));
            movieItem.setPopularity(mCursor
                    .getDouble(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_POPULARITY)));
            movieItem.setOverview(mCursor
                    .getString(mCursor.getColumnIndex(MovieEntries.COLUMN_MOVIE_SYNOPSIS)));
            movies.add(movieItem);
        }
        return movies;
    }


    /**
     * A method to check movie ID in table
     * is exist or not exist
     */
    private boolean checkIfDataIsFavorite(String searchItem){
        String[] columns = { MovieEntries.COLUMN_MOVIE_ID };
        String selection = MovieEntries.COLUMN_MOVIE_ID + " =?";
        String[] selectionArgs = { searchItem };

        Cursor mCursor = getContentResolver().query(
                MovieContract.MovieEntries.CONTENT_URI,  // The content URI of the words table
                columns,                       // The columns to return for each row
                selection,                   // Either null, or the word the user entered
                selectionArgs,                    // Either empty, or the string the user entered
                MovieContract.MovieEntries.COLUMN_MOVIE_ID);

        boolean exists = (mCursor.getCount() > 0);
        mCursor.close();

        return exists;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getMovieData();
    }
}
