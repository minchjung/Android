package com.example.threadfragment03;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES); // 실행자 java
    Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper()); //  새 UI 갱신 Helper
}
