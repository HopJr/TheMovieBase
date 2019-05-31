package com.example.blago.themoviedb.LogIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.blago.themoviedb.Account.Account;
import com.example.blago.themoviedb.MainActivity;
import com.example.blago.themoviedb.R;
import com.example.blago.themoviedb.Retrofit.NetworkConstants;
import com.example.blago.themoviedb.Retrofit.RetrofitClient;
import com.example.blago.themoviedb.Retrofit.TMDBApi;
import com.example.blago.themoviedb.Token.Session;
import com.example.blago.themoviedb.Token.Token;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LogInActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private CompositeDisposable compositeDisposable;
    private TMDBApi mService;
    private WebView web_view;
    private WebSettings webSettings;
    private Token mToken;
    SharedPreferences sp;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_layout);

        initComponents();
        createToken();

        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("/allow")){
                    createNewSeasion();
                }
            }
        });
    }

    private void initComponents() {
        retrofit = RetrofitClient.getInstance();
        compositeDisposable = new CompositeDisposable();
        mService = retrofit.create(TMDBApi.class);
        web_view = (WebView) findViewById(R.id.web_view);
        web_view.setWebViewClient(new WebViewClient());
        webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = sp.edit();
    }

    private void createToken() {
        compositeDisposable.add(mService.getAuthToken(NetworkConstants.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Token>() {
                    @Override
                    public void accept(Token token) throws Exception {
                        mToken = new Token();
                        mToken.setRequest_token(token.getRequest_token());
                        web_view.loadUrl(NetworkConstants.LOGIN_PAGE + token.getRequest_token());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    private void createNewSeasion(){
       compositeDisposable.add(mService.getSession(mToken.getRequest_token(), NetworkConstants.API_KEY)
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Consumer<Session>() {
           @Override
           public void accept(Session session) throws Exception {
               mEditor.putString("session_id", session.getSession_id());
               mEditor.commit();
               getAccount(session);
           }
       }, new Consumer<Throwable>() {
           @Override
           public void accept(Throwable throwable) throws Exception {

           }
       }));
    }

    private void getAccount(Session session){
        compositeDisposable.add(mService.getAccount(NetworkConstants.API_KEY, session.getSession_id())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Account>() {
            @Override
            public void accept(Account account) throws Exception {
                 mEditor.putString("name", account.getName());
                 mEditor.putString("username", account.getUsername());
                 mEditor.putLong("id", account.getId());
                 mEditor.putString("avatar", account.getAvatar().getGravatar().getHash());
                 mEditor.commit();

                 Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                 startActivity(intent);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        }));
    }

    @Override
    public void onBackPressed() {
        if (web_view.canGoBack()) {
            super.onBackPressed();
        }else {
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
