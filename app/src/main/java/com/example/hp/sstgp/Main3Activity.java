package com.example.hp.sstgp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Main3Activity extends AppCompatActivity {
    private WebView webView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_test);
        webView1=(WebView) findViewById(R.id.webView);
        webView1.setWebViewClient(new WebViewClient());
        webView1.loadUrl("https://www.google.com/");
        WebSettings webSettings= webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
    @Override
    public void onBackPressed() {
        if(webView1.canGoBack()){
            webView1.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}
