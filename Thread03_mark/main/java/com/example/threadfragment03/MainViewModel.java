package com.example.threadfragment03;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.threadfragment03.repository.RepositoryCallback;
import com.example.threadfragment03.repository.ThreadRepo;

import java.util.ArrayList;

public class MainViewModel  extends AndroidViewModel {

    private final ThreadRepo repo; // Thread 에서의 작업을 구체화 하고 있는 객체 (추상 class Result 와 interface RepositortyCallback 구체화 하고 있는 객체 )
    public MutableLiveData<ArrayList<Integer>> progressLiveData = new MutableLiveData<ArrayList<Integer>>(); // 라이브데이터 라이브러리

    public MainViewModel(@NonNull Application application) {
        super(application);
        repo = new ThreadRepo(
                ((App) application).mainThreadHandler,
                ((App) application).executorService
        ); // 반드시 하나의 Tread 실행시 하나의 Handler 같이 생성해 넘겨 주기 위한 의존성 주입
    }
    public void longTask(){  // UI 랜더링
        repo.longTask(new RepositoryCallback<ArrayList<Integer>>() { // Callback 함수 Overriding  (==>ThreadRepo)
            @Override
            public void onComplete(Result<ArrayList<Integer>> result) { // <-- onComplete 구체작업을 실행 (Callback -> ThrerePo 쓰레드 완료후 구체화된 작업이 여기서 마지막으로 처리됨)
                if (result instanceof Result.Success) { //결과 넘어온  result = Success 객체와 같다면 (자바 instanceof test 구문, Success 와 Error 는 Class 객체 ** )
                    progressLiveData.postValue(((Result.Success<ArrayList<Integer>>) result).data); //<-- postValue 로 result.data를 넘겨준다 (data = ArrayList<Integer> 추 후 105개 단위로 짤라야함) !
                } else if (result instanceof Result.Error) {
                    //error
                }
            }
        });
    }
}
