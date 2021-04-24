package com.example.lec06_webview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText mEditAddr ;
    Button mBtnWebView;
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditAddr = findViewById(R.id.editAddr);
        mBtnWebView = findViewById(R.id.btn_webView);
        mWebView = findViewById(R.id.webView);
        // web 설정 과정 :
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); //javascript 활성

        //1.[WebView]
        // 실질적으로 webView 객체를 활용해 setWeViewClient 의 메소드를 쓰기 위함이라 봐도 될것같다.
        // 여러가지 처리해줄 수 있는 method들이 다양하게 존재한다.
        mWebView.setWebViewClient(new WebViewClient(){
            // basic 기능 구현
            @Override // web page loading 처리
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(MainActivity.this, "로딩 완료", Toast.LENGTH_SHORT).show();
            }
            @Override // nothing
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }
        });
//      2.[EditText]
        // actionSearch  event 처리
        mEditAddr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){ // imOption 속성->actionSearch 설정해줬음
                    mBtnWebView.callOnClick(); // 버튼 클릭함수 호출

                    // 키보드 숨기기 (단순 덩어리 복붙 사용 )
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true; // <-- 이벤트 항상 소모 시켜 reset
                }
                return false;
            }
        });
    }// onCreate 생명주기 end

    // Main 벗어난 후
    //[ web 이동 ]- 웹 load
    public void moveWebView(View view) {
        String url = mEditAddr.getText().toString();
        if( !url.startsWith("http://") ){ url= "http://"+url; }
        mWebView.loadUrl(url);
    }
    // [ web 이동 ]- 뒤로가기
    // webView history 로 이동
    @Override
    public void onBackPressed() { // 안드로이드 기본 뒤로가기 함수 override
        if(mWebView.canGoBack() ){ mWebView.goBack();  }
        else{ super.onBackPressed();}
    }
    // [ Menu ] 설정
    @Override  // 생성
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
//    [ Menu ]  event 처리
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            // 뒤로가기
            case R.id.action_back: // 위 동일
                if(mWebView.canGoBack() ){ mWebView.goBack();  }
                return true;
            case R.id.action_forward: // 앞
                if(mWebView.canGoForward()){ mWebView.goForward(); }
                return true;
            case R.id.action_refresh: // 새로 고침
                mWebView.reload();
                return true;
        } // case 모두 return <-- event 주기 reset 처리
        return super.onOptionsItemSelected(item); // 그런것 아니면 재귀로 event 상태 reset
    }
}