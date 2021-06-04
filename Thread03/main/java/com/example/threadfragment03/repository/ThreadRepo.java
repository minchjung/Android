package com.example.threadfragment03.repository;

import com.example.threadfragment03.Result;
import android.os.Handler;
import java.util.concurrent.Executor;

import java.util.ArrayList;
import java.util.Random;


public class ThreadRepo {
    private final Handler resultHandler;
    private final Executor executor;
    private Random rand = new Random();
    private ArrayList<Integer> dataArr ;

    public ThreadRepo(Handler resultHandler, Executor executor){
        this.executor = executor;
        this.resultHandler = resultHandler;
    }

    public void longTask(RepositoryCallback<ArrayList<Integer>> callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    int num = 0 ;
                    dataArr = new ArrayList<Integer>();
                    for (int i = 0; i < 10500; i++) {
                        // Callback to update UI with the data
                        dataArr.add(rand.nextInt(256)) ;
                    }
                    Result<ArrayList<Integer>> result = new Result.Success<ArrayList<Integer>>(dataArr);
                    notifyResult(result, callback);
                    Thread.sleep(30);
                }
                catch (Exception e){
                    Result<ArrayList<Integer>> result = new Result.Error<>(e);
                    notifyResult(result,callback);
                }
            }
        });
    }

    private void notifyResult(
            final Result<ArrayList<Integer>> result,
            final RepositoryCallback<ArrayList<Integer>> callback
    ) {
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }
}


