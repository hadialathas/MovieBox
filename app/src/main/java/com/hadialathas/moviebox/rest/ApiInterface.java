package com.hadialathas.moviebox.rest;

/**
 * Created by hadialathas on 7/1/17.
 */

import com.hadialathas.moviebox.model.MoviesResponse;
import com.hadialathas.moviebox.model.ReviewsResponse;
import com.hadialathas.moviebox.model.TrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailersResponse> getMovieVideos(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey);


}