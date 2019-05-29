package com.example.blago.themoviedb.Retrofit;

import com.example.blago.themoviedb.MovieModel.SearchMovie;
import com.example.blago.themoviedb.YoutubeModel.YoutubeModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeApi {


    @GET("search")
    Observable<YoutubeModel> searchYoutube(@Query("part") String part,
                                           @Query("type") String type,
                                           @Query("key") String key,
                                           @Query("q") String q);
}
