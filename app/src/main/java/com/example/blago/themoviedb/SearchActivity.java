package com.example.blago.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blago.themoviedb.Adapter.SearchAdapter;
import com.example.blago.themoviedb.Helper.RecyclerTouchListener;
import com.example.blago.themoviedb.MovieModel.Result;
import com.example.blago.themoviedb.MovieModel.SearchMovie;
import com.example.blago.themoviedb.Retrofit.NetworkConstants;
import com.example.blago.themoviedb.Retrofit.RetrofitClient;
import com.example.blago.themoviedb.Retrofit.TMDBApi;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private ArrayList<Result> mList;
    private EditText edit_text_search;
    private ImageButton img_btn_back;
    CompositeDisposable compositeDisposable;
    TMDBApi mService;
    int page = 1;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        initComponents();
        getSearchItem();
        addListener();
    }

    private void initComponents() {
        mList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        img_btn_back = (ImageButton) findViewById(R.id.img_btn_back);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        retrofit = RetrofitClient.getInstance();
        compositeDisposable = new CompositeDisposable();
        mService = retrofit.create(TMDBApi.class);
    }

    private void getSearchItem() {
        mList.addAll(getIntent().<Result>getParcelableArrayListExtra("resoult"));
        String search = getIntent().getStringExtra("search");
        adapter = new SearchAdapter(getApplicationContext(), mList);
        recyclerView.setAdapter(adapter);
        edit_text_search.setText(search);
    }

    private void addListener() {
        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSearchInfromation();
                    return true;
                }
                return false;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(SearchActivity.this, MovieDetailsActivity.class);
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

    private void getSearchInfromation() {
        compositeDisposable.add(mService.searchMovie(NetworkConstants.API_KEY, NetworkConstants.LANGUAGE,
                edit_text_search.getText().toString(), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SearchMovie>() {
                    @Override
                    public void accept(SearchMovie searchMovie) throws Exception {
                        mList.clear();
                        mList.addAll(searchMovie.getResults());
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getPoster_path() == null) {
                                mList.remove(i);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }
}
