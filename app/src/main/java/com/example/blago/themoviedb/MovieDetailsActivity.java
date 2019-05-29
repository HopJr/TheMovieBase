package com.example.blago.themoviedb;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blago.themoviedb.Adapter.CompanyAdapter;
import com.example.blago.themoviedb.MovieModel.MovieDetails;
import com.example.blago.themoviedb.MovieModel.ProductionCompany;
import com.example.blago.themoviedb.MovieModel.Result;
import com.example.blago.themoviedb.Retrofit.NetworkConstants;
import com.example.blago.themoviedb.Retrofit.RetrofitClient;
import com.example.blago.themoviedb.Retrofit.TMDBApi;
import com.example.blago.themoviedb.Retrofit.YoutubeApi;
import com.example.blago.themoviedb.YoutubeModel.YoutubeModel;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MovieDetailsActivity extends YouTubeBaseActivity {


    private ImageView img_movie;
    private TextView txt_name, txt_overviewStatus, txt_overview, txt_status, txt_original_language, txt_run_time,
            txt_budget, txt_revenue;
    private android.support.v7.widget.Toolbar toolbar;
    private ImageButton img_back;
    private YouTubePlayerView youTubePlayer;
    CompositeDisposable compositeDisposable, compositeDisposable_youtube;
    TMDBApi mService;
    YoutubeApi mService_youtube;
    Retrofit retrofit, retrofit_youtube;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private ArrayList<ProductionCompany> mlist;
    private CompanyAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_layout);

        initComponents();
        getParacable();
        addListeners();
    }

    private void initComponents() {
        retrofit = RetrofitClient.getInstance();
        retrofit_youtube = RetrofitClient.getInstance_youtube();
        mService_youtube = retrofit_youtube.create(YoutubeApi.class);
        compositeDisposable_youtube = new CompositeDisposable();
        mlist = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_companys);
        img_back = (ImageButton) findViewById(R.id.img_back);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new CompanyAdapter(getApplicationContext(), mlist);
        recyclerView.setAdapter(adapter);
        compositeDisposable = new CompositeDisposable();
        mService = retrofit.create(TMDBApi.class);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.parseColor("#263248"), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);
        img_movie = (ImageView) findViewById(R.id.img_movie);
        youTubePlayer = (YouTubePlayerView) findViewById(R.id.youtube_player);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_revenue = (TextView) findViewById(R.id.txt_revenue);
        txt_budget = (TextView) findViewById(R.id.txt_budget);
        txt_original_language = (TextView) findViewById(R.id.txt_original_language);
        txt_run_time = (TextView) findViewById(R.id.txt_run_time);
        txt_status = (TextView) findViewById(R.id.txt_status);
        txt_overviewStatus = (TextView) findViewById(R.id.txt_overviewStatus);
        txt_overview = (TextView) findViewById(R.id.txt_overview);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

    }

    private void getParacable() {
        Result result = new Result();
        result = getIntent().getParcelableExtra("result");
        int id = (int) result.getId();

        compositeDisposable.add(mService.getMovieDetails(id, NetworkConstants.API_KEY, NetworkConstants.LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieDetails>() {
                    @Override
                    public void accept(MovieDetails movieDetails) throws Exception {
                        fillData(movieDetails);
                        progressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        getYoutubeTrailer(movieDetails);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    private void fillData(MovieDetails movieDetails) {
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + movieDetails.getBackdrop_path()).into(img_movie);
        String year = movieDetails.getRelease_date();
        year = year.substring(0, year.indexOf("-"));
        txt_name.setText(movieDetails.getTitle() + " " + "(" + year + ")");
        txt_overview.setText(movieDetails.getOverview());
        txt_overviewStatus.setText("Overview");
        txt_status.setText("Status: " + movieDetails.getStatus());
        txt_original_language.setText("Original Language: " + movieDetails.getOriginal_language());
        txt_run_time.setText("Runtime: " + movieDetails.getRuntime() + " " + "min");
        String budget = String.valueOf(movieDetails.getBudget());
        double amount = Double.parseDouble(budget);
        DecimalFormat formatter = new DecimalFormat("#,###");
        txt_budget.setText("Budget: " + formatter.format(amount));
        String revenue = String.valueOf(movieDetails.getRevenue());
        double amount_of_revenue = Double.parseDouble(revenue);
        txt_revenue.setText("Revenue: " + formatter.format(amount_of_revenue));
        mlist.addAll(movieDetails.getProduction_companies());
        adapter.notifyDataSetChanged();
    }

    private void addListeners() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void getYoutubeTrailer(MovieDetails movieDetails) {

        compositeDisposable_youtube.add(mService_youtube.searchYoutube("snippet", "video",
                NetworkConstants.API_YOUTUBE_KEY, movieDetails.getOriginal_title() + " " + "trailer")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<YoutubeModel>() {
                    @Override
                    public void accept(YoutubeModel youtubeModel) throws Exception {
                        loadYoutubeVideoToPlayer(youtubeModel);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    private void loadYoutubeVideoToPlayer(final YoutubeModel youtubeModel) {
        youTubePlayer.initialize(NetworkConstants.API_YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(youtubeModel.items.get(1).getId().videoId);
                youTubePlayer.pause();
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }
}



