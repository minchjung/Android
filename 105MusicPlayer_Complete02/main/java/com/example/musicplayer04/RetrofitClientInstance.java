package com.example.musicplayer04;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    // field and method all static so that we can use with the object by itself
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/";

    // Method to Initialize Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}