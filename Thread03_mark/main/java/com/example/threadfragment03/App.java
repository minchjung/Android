package com.example.threadfragment03;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // 상위 Application을 상속 받는 객체 에서 부터 쓰레드 를 생성 해 줘야 한다. 
    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES); // 실행자 java
    Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper()); //  새 UI 갱신 Helper
}
