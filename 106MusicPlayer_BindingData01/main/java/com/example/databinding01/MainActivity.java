package com.example.databinding01;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.databinding01.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static ActivityMainBinding mainBinding;
    public static Context context;

    MusicData musicData;
//    MediaPlayerService playerService;
    private boolean isServiceBound=false;

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//
//            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
//            playerService = binder.getService();
//            isServiceBound = true;
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isServiceBound = false;
//        }
//    };
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = this;


        //Set MusicData from Json Parsing
        Api_interface api_interface = RetrofitClientInstance.getRetrofitInstance().create(Api_interface.class);
        Call<MusicData> call = api_interface.getMusicData();
        call.enqueue(new Callback<MusicData>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MusicData> call, Response<MusicData> response) {
                musicData = response.body();
                mainBinding.setVarXML(musicData);

                musicData.setLyricsMap(musicData.getLyrics());
                mainBinding.imageBtn.setImageResource(R.drawable.ic_pause);
                mainBinding.lyrics2.setText(musicData.getLyricsList().get(0));
                connToService(musicData.getFile());

            }

            @Override
            public void onFailure(Call<MusicData> call, Throwable t) {
            }
        });
    }//onCreate


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void connToService(String url) {
        //Check is service is active
//        if (isServiceBound) return;

        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        playerIntent.putExtra("mediaURL", url);
        playerIntent.putExtra("musicData", musicData);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(playerIntent);
        else
            startService(playerIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MediaPlayerService.class);
        stopService(intent);
//        unbindService(serviceConnection);
    }

}

