package com.example.blago.themoviedb.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    private static Retrofit instance, instance_youtube;
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String BASE_YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/";


    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }

    public static Retrofit getInstance_youtube() {

        if (instance_youtube == null) {
            instance_youtube = new Retrofit.Builder()
                    .baseUrl(BASE_YOUTUBE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return instance_youtube;
    }
}
