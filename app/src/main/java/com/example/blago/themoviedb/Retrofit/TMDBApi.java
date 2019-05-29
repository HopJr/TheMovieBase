package com.example.blago.themoviedb.Retrofit;

import com.example.blago.themoviedb.MovieModel.MovieDetails;
import com.example.blago.themoviedb.MovieModel.MovieModelNowPlaying;
import com.example.blago.themoviedb.MovieModel.MovieModelPopular;
import com.example.blago.themoviedb.MovieModel.MovieModelTopRated;
import com.example.blago.themoviedb.MovieModel.MovieModelUpcoming;
import com.example.blago.themoviedb.MovieModel.SearchMovie;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBApi {

    @GET("movie/top_rated")
    Observable<MovieModelTopRated> getTopRated(@Query("api_key") String api_key,
                                                      @Query("language") String language,
                                                      @Query("page") int page);


    @GET("movie/{movie_id}")
    Observable<MovieDetails> getMovieDetails(@Path("movie_id") long movieid,
                                             @Query("api_key") String api_key,
                                             @Query("language") String language);


    @GET("movie/now_playing")
    Observable<MovieModelNowPlaying> getNowPlaying(@Query("api_key") String api_key,
                                                   @Query("language") String language,
                                                   @Query("page") int page);


    @GET("movie/upcoming")
    Observable<MovieModelUpcoming> getUpcoming(@Query("api_key") String api_key,
                                               @Query("language") String language,
                                               @Query("page") int page);

    @GET("movie/popular")
    Observable<MovieModelPopular> getPopular(@Query("api_key") String api_key,
                                             @Query("language") String language,
                                             @Query("page") int page);


    @GET("search/movie")
    Observable<SearchMovie> searchMovie(@Query("api_key") String api_key,
                                        @Query("language") String language,
                                        @Query("query") String query,
                                        @Query("page") int page);

}
