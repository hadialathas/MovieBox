package com.hadialathas.moviebox.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hadialathas on 8/8/17.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.hadialathas.moviebox";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // This is the path for the "tasks" directory
    public static final String PATH_FAVORITES = "favorites";

    public static final class MovieEntries implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_BACKGROUND_IMAGE = "movieBackgroundImage";
        public static final String COLUMN_MOVIE_POSTER_IMAGE = "moviePosterImage";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_VOTE_AVG = "movieVoteAverage";
        public static final String COLUMN_MOVIE_VOTE_COUNT= "movieVoteCount";
        public static final String COLUMN_MOVIE_POPULARITY= "moviePopularity";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_IS_FAVORITE = "movieIsFavorite";

    }
}
