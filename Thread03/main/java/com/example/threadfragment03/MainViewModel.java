package com.example.threadfragment03;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.threadfragment03.repository.RepositoryCallback;
import com.example.threadfragment03.repository.ThreadRepo;

import java.util.ArrayList;

public class MainViewModel  extends AndroidViewModel {

    private final ThreadRepo repo;
    public MutableLiveData<ArrayList<Integer>> progressLiveData = new MutableLiveData<ArrayList<Integer>>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repo = new ThreadRepo(
                ((App) application).mainThreadHandler,
                ((App) application).executorService
        );
    }
    public void longTask(){
        repo.longTask(new RepositoryCallback<ArrayList<Integer>>() {
            @Override
            public void onComplete(Result<ArrayList<Integer>> result) {
                if (result instanceof Result.Success) {
                    progressLiveData.postValue(((Result.Success<ArrayList<Integer>>) result).data);
                } else if (result instanceof Result.Error) {
                    //error
                }
            }
        });
    }
}
