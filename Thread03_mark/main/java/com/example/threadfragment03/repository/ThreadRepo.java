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
        this.resultHandler = resultHandler; // Constructor DI
    }
    //시간이 오래 걸릴 수 있는 구체적인 작업을 진행
    public void longTask(RepositoryCallback<ArrayList<Integer>> callback){ // 콜백 객체를 끼고
        executor.execute(new Runnable() { // 쓰레드 실행
            @Override
            public void run() {
                try{
                    int num = 0 ;
                    dataArr = new ArrayList<Integer>(); // 데이터를 여기서 받아오고 작업
                    for (int i = 0; i < 10500; i++) { // <--임의로 105 *100 개 세트를 받아 봄
                        // Callback to update UI with the data
                        dataArr.add(rand.nextInt(256)) ;
                    }
                    Result<ArrayList<Integer>> result = new Result.Success<ArrayList<Integer>>(dataArr); // 성공시 ArrayList 를 Result.data  형태로 넘겨 줄 수 있다.
                    notifyResult(result, callback); //단, UI인 메인 쓰레드에서 빠져 나온 쓰레드 이기 때문에 UI 랜더링이 불가하다.
                    Thread.sleep(30);
                }
                catch (Exception e){
                    Result<ArrayList<Integer>> result = new Result.Error<>(e);
                    notifyResult(result,callback);
                }
            }
        });
    }
    // 쓰레드를 빠져나와서 핸들러를 통해 해당 쓰레드 (둘은 1:1 관계)에서의 처리 데이터를 post run 으로 또 다른 쓰레드 작업을 걸어줌
    private void notifyResult(
            final Result<ArrayList<Integer>> result,
            final RepositoryCallback<ArrayList<Integer>> callback
    ) {
        resultHandler.post(new Runnable() {
            @Override //Interface onComplete <-- 작업 완료시 실행문을 구체화 해주는데 Excutor  쓰레드에서의 Callback 과 항상 의존 관계임 을 알 수 있다.
            public void run() {
                callback.onComplete(result);
            }
        });
    }
}


