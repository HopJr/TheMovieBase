package com.example.blago.themoviedb.Retrofit;

import com.example.blago.themoviedb.Account.Account;
import com.example.blago.themoviedb.MovieModel.FavoriteMovies;
import com.example.blago.themoviedb.MovieModel.MovieDetails;
import com.example.blago.themoviedb.MovieModel.MovieModelNowPlaying;
import com.example.blago.themoviedb.MovieModel.MovieModelPopular;
import com.example.blago.themoviedb.MovieModel.MovieModelTopRated;
import com.example.blago.themoviedb.MovieModel.MovieModelUpcoming;
import com.example.blago.themoviedb.MovieModel.SearchMovie;
import com.example.blago.themoviedb.Token.Session;
import com.example.blago.themoviedb.Token.Token;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET("authentication/token/new")
    Observable<Token> getAuthToken(@Query("api_key") String api_key);


    @GET("authentication/session/new")
    Observable<Session> getSession(@Query("request_token") String requestToken,
                                   @Query("api_key")String api_key);


    @GET("account")
    Observable<Account> getAccount(@Query("api_key") String api_key,
                                   @Query("session_id") String session_id);


    @GET("account/{account_id}/favorite/movies")
    Observable<FavoriteMovies> getFavoriteAccountMovies(@Path("account_id") int account_id,
                                                        @Query("api_key") String api_key,
                                                        @Query("session_id") String session_id);


}
