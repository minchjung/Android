package com.example.threadfragment03.repository;

import com.example.threadfragment03.Result;

public interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}
