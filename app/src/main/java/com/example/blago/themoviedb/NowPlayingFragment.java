package com.example.blago.themoviedb;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blago.themoviedb.Adapter.TopRatedAdapter;
import com.example.blago.themoviedb.Helper.RecyclerTouchListener;
import com.example.blago.themoviedb.MovieModel.MovieModelNowPlaying;
import com.example.blago.themoviedb.MovieModel.MovieModelTopRated;
import com.example.blago.themoviedb.MovieModel.Result;
import com.example.blago.themoviedb.Retrofit.NetworkConstants;
import com.example.blago.themoviedb.Retrofit.RetrofitClient;
import com.example.blago.themoviedb.Retrofit.TMDBApi;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    TMDBApi mService;
    ArrayList<Result> list;
    private int page = 1;
    private RecyclerView recyclerView;
    private TopRatedAdapter adapter;
    private GridLayoutManager mLinearLayoutManager;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    static NowPlayingFragment instance;


    public static NowPlayingFragment getInstance() {
        instance = new NowPlayingFragment();


        return instance;
    }

    public NowPlayingFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(TMDBApi.class);
        list = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        initComponents(itemView);
        getNowPlayingMovies();
        addListener();
        return itemView;
    }

    private void getNowPlayingMovies() {
        compositeDisposable.add(mService.getNowPlaying(NetworkConstants.API_KEY, NetworkConstants.LANGUAGE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieModelNowPlaying>() {
                    @Override
                    public void accept(MovieModelNowPlaying movieModelNowPlaying) throws Exception {
                        list.addAll(movieModelNowPlaying.getResults());
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPoster_path() == null) {
                                list.remove(i);
                            }
                        }
                        setAdapter();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void initComponents(View view) {
        mLinearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new TopRatedAdapter(getContext(), list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void addListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = mLinearLayoutManager.getChildCount();
                    totalItemCount = mLinearLayoutManager.getItemCount();
                    pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                }

                if (loading) {
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                        loading = false;
                        page = page + 1;
                        getNowPlayingMovies();
                        recyclerView.setLayoutManager(mLinearLayoutManager);
                    }
                }
                loading = true;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                intent.putExtra("result", list.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
