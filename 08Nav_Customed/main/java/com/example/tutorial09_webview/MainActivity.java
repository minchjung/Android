package com.example.tutorial09_webview;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String url = "http://www.naver.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.webView);
        // javascript enable
        webView.getSettings().setJavaScriptEnabled(true);
        //url load
        webView.loadUrl(url);
        // optimized chrome
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientClass()); // <--should make the inner Class
    }
    // if user want to go back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // back button is BEYCODE_BACK so press back key and when webView can go back
        if(keyCode == KeyEvent.KEYCODE_BACK  &&  webView.canGoBack()){
            // make that happen
            webView.goBack();
        }
        return true; // <---true
    }
    // declare innerClass
    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // get the current page url . just in case you need (often use this )
             return true; // <--true
        }
    }
    // you should go to mainfests- AndroidMainfest.xml and let it have Internet permission as below
    //        <uses-permission android:name="android.permission.INTERNET"/>
}