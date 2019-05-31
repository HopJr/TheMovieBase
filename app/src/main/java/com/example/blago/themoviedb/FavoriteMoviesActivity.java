package com.example.blago.themoviedb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.example.blago.themoviedb.Account.Account;
import com.example.blago.themoviedb.Adapter.BookmarkAdapter;
import com.example.blago.themoviedb.Helper.RecyclerTouchListener;
import com.example.blago.themoviedb.MovieModel.FavoriteMovies;
import com.example.blago.themoviedb.MovieModel.Result;
import com.example.blago.themoviedb.Retrofit.NetworkConstants;
import com.example.blago.themoviedb.Retrofit.RetrofitClient;
import com.example.blago.themoviedb.Retrofit.TMDBApi;
import com.example.blago.themoviedb.Token.Session;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class FavoriteMoviesActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private CompositeDisposable compositeDisposable;
    private TMDBApi mService;
    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    SharedPreferences sp;
    SharedPreferences.Editor mEditor;
    private Account mAccount;
    private ArrayList<Result> mList;
    private Session mSession;
    private ImageButton img_btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_movies_layout);

        initComponents();
        getBookmarkMovies();
        addListener();
    }


    private void initComponents(){
        mAccount = new Account();
        mSession = new Session();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = sp.edit();
        mAccount = new Account();
        mList = new ArrayList<>();
        adapter = new BookmarkAdapter(getApplicationContext(), mList);
        img_btn_back = (ImageButton) findViewById(R.id.img_btn_back);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        mAccount.setId(sp.getLong("id",0));
        mSession.setSession_id(sp.getString("session_id",""));
        retrofit = RetrofitClient.getInstance();
        compositeDisposable = new CompositeDisposable();
        mService = retrofit.create(TMDBApi.class);
    }

    private void getBookmarkMovies(){
        compositeDisposable.add(mService.getFavoriteAccountMovies((int) mAccount.getId(), NetworkConstants.API_KEY, mSession.getSession_id())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<FavoriteMovies>() {
            @Override
            public void accept(FavoriteMovies favoriteMovies) throws Exception {
                mList.addAll(favoriteMovies.getResults());
                recyclerView.setAdapter(adapter);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }));
    }

    private void addListener(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(FavoriteMoviesActivity.this, MovieDetailsActivity.class);
                intent.putExtra("result", mList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        img_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
