package com.example.blago.themoviedb.Token;

import com.example.blago.themoviedb.Retrofit.NetworkConstants;
import com.example.blago.themoviedb.Retrofit.RetrofitClient;
import com.example.blago.themoviedb.Retrofit.TMDBApi;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Token {

    public boolean success;
    public String expires_at;
    public String request_token;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public String getRequest_token() {
        return request_token;
    }

    public void setRequest_token(String request_token) {
        this.request_token = request_token;
    }

}
