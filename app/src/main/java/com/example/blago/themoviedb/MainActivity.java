package com.example.blago.themoviedb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.blago.themoviedb.Account.Account;
import com.example.blago.themoviedb.Adapter.ViewPagerAdapter;
import com.example.blago.themoviedb.LogIn.LogInActivity;
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

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView txt_app_name;
    private NavigationView nav_view;
    private EditText edit_text_search;
    private RelativeLayout layout;
    private ImageButton img_search, img_btn_back;
    ActionBarDrawerToggle toggle;
    CompositeDisposable compositeDisposable;
    TMDBApi mService;
    Retrofit retrofit;
    private Account mAccount;
    SharedPreferences sp;
    SharedPreferences.Editor mEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        setupViewPager();
        addListener();

    }

    private void initComponents() {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = sp.edit();
        mAccount = new Account();
        mAccount.setId(sp.getLong("id",0));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setItemIconTintList(null);
        if(mAccount.getId() == 0){
            nav_view.getMenu().findItem(R.id.nav_login).setVisible(true);
            nav_view.getMenu().findItem(R.id.nav_my_profile).setVisible(false);
            nav_view.getMenu().findItem(R.id.nav_logout).setVisible(false);
            nav_view.getMenu().findItem(R.id.nav_favorite).setVisible(false);
        }else{
            nav_view.getMenu().findItem(R.id.nav_login).setVisible(false);
            nav_view.getMenu().findItem(R.id.nav_my_profile).setVisible(true);
            nav_view.getMenu().findItem(R.id.nav_logout).setVisible(true);
            nav_view.getMenu().findItem(R.id.nav_favorite).setVisible(true);
        }
        layout = (RelativeLayout) findViewById(R.id.relative_layout);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        img_btn_back = (ImageButton) findViewById(R.id.img_btn_back);
        txt_app_name = (TextView) findViewById(R.id.txt_app_name);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        img_search = (ImageButton) findViewById(R.id.img_search);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
        retrofit = RetrofitClient.getInstance();
        compositeDisposable = new CompositeDisposable();
        mService = retrofit.create(TMDBApi.class);
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TopRatedFragment.getInstance(), "Top Rated Movies");
        adapter.addFragment(NowPlayingFragment.getInstance(), "Now Playing Movies");
        adapter.addFragment(UpcomingMovieFragment.getInstance(), "Upcoming Movies");
        adapter.addFragment(PopularMovieFragment.getInstance(), "Popular Movies");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void addListener() {
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
        });

        img_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                edit_text_search.setText("");
            }
        });

        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    compositeDisposable.add(mService.searchMovie(NetworkConstants.API_KEY, NetworkConstants.LANGUAGE,
                            edit_text_search.getText().toString(), 1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<SearchMovie>() {
                                @Override
                                public void accept(SearchMovie searchMovie) throws Exception {
                                    ArrayList<Result> list = new ArrayList<>();
                                    list.addAll(searchMovie.getResults());
                                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                                    intent.putExtra("resoult", list);
                                    intent.putExtra("search", edit_text_search.getText().toString());
                                    startActivity(intent);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                }
                            }));

                    return true;
                }
                return false;
            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_now_playing: {
                        switchPage(item);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_popular: {
                        switchPage(item);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_top_rated: {
                        switchPage(item);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_upcoming: {
                        switchPage(item);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_login: {
                        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.nav_favorite: {
                        Intent intent = new Intent(MainActivity.this, FavoriteMoviesActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_logout: {
                        mEditor.clear();
                        mEditor.commit();
                        nav_view.getMenu().findItem(R.id.nav_login).setVisible(true);
                        nav_view.getMenu().findItem(R.id.nav_my_profile).setVisible(false);
                        nav_view.getMenu().findItem(R.id.nav_logout).setVisible(false);
                        nav_view.getMenu().findItem(R.id.nav_favorite).setVisible(false);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_my_profile: {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    }
                }
                return true;
            }
        });

    }

    private void switchPage(MenuItem item) {
        for (int i = 0; i < adapter.getFragmentTittle().size(); i++) {
            if (item.getTitle().toString().equalsIgnoreCase(adapter.getFragmentTittle().get(i))) {
                viewPager.setCurrentItem(i);
            }
        }
    }
}
