package com.example.threadfragment03.repository;

import com.example.threadfragment03.Result;

public interface RepositoryCallback<T> { //Callback & 완료  Interface
    void onComplete(Result<T> result);
}
