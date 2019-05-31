package com.example.blago.themoviedb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.blago.themoviedb.Account.Account;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor mEditor;
    private WebView web_view;
    private WebSettings webSettings;
    private Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        initComponents();

    }

    private void initComponents() {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = sp.edit();
        mAccount = new Account();
        mAccount.setUsername(sp.getString("username",""));
        web_view = (WebView) findViewById(R.id.web_view);
        web_view.setWebViewClient(new WebViewClient());
        webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web_view.loadUrl("https://www.themoviedb.org/u/" + mAccount.getUsername());
    }

    @Override
    public void onBackPressed() {
        if(web_view.canGoBack()) {
            super.onBackPressed();
        }else {
            finish();
        }
    }
}
