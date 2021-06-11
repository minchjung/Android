package com.example.musicplayer04;

//retrofit2 library  (출저: https://github.com/square/retrofit)
import retrofit2.Call;
import retrofit2.http.GET;

public interface Api_interface {
    //GET mapping
    @GET("2020-flo/song.json")
    Call<MusicData> getMusicData();
    //       URI: https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json
}
