package com.hadialathas.moviebox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hadialathas.moviebox.data.MovieContract.MovieEntries;

/**
 * Created by hadialathas on 8/8/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntries.TABLE_NAME
                + " (" + MovieEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieEntries.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_BACKGROUND_IMAGE + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_POSTER_IMAGE + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_VOTE_AVG + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_VOTE_COUNT + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_POPULARITY + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, "
                + MovieEntries.COLUMN_MOVIE_IS_FAVORITE + " TEXT NOT NULL"
                + ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieEntries.TABLE_NAME);
        onCreate(db);

    }
}
