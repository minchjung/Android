package com.example.musicplayer04;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//glide library (출저:https://github.com/bumptech/glide)
//retrofit2 library  (출저: https://github.com/square/retrofit)

public class MainActivity extends AppCompatActivity {

    private TextView singer, title, album, lyrics1, lyrics2;
    private ImageView albumImg;
    private ImageButton imageBtn;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;

    private boolean hasMediaStarted = false;
    private Animation slideUp, slideUp2,fadeIn, fadeOut;

    MusicData musicData;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get this activity context for Glide using in different Thread
        Context actContext = this;

        //Initialize view
        singer = findViewById(R.id.singer);
        title = findViewById(R.id.title);
        album = findViewById(R.id.album);
        albumImg = findViewById(R.id.albumImg);
        lyrics1 = findViewById(R.id.lyrics1);
        lyrics2 = findViewById(R.id.lyrics2);

        imageBtn = findViewById(R.id.imageBtn);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //Initialize Animation
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup);
        slideUp2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup2);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);

        //Initialize handler
        handler = new Handler();

        //Initialize retrofit2
        Api_interface myAPIService = RetrofitClientInstance.getRetrofitInstance().create(Api_interface.class);


        //Request and Get Response(Json) && set Data
        Call<MusicData> call = myAPIService.getMusicData();
        call.enqueue(new Callback<MusicData>() { //results into enqueue

            @Override // Success - 1. set Init of UI,   2. set MediaPlayer from Button Click    3. set SeeckBar
            public void onResponse(Call<MusicData> call, Response<MusicData> response) {
                // Initialize MusicData on Response
                musicData = response.body();

                // UI update
                title.setText(musicData.getTitle());
                singer.setText(musicData.getSinger());
                album.setTypeface(null, Typeface.ITALIC);
                album.setText("album:  " + musicData.getAlbum());
                Glide.with(actContext).load(musicData.getImage()).into(albumImg);

                // Lyrics needs Parsing
                musicData.setLyricsMap(musicData.getLyrics());
//                Log.v("lyrics", musicData.getLyrics());
                lyrics1.setText(musicData.getLyricsMap().get(musicData.getHashMap_timeRef().get(0)));
//                lyrics2.setText(musicData.getLyricsMap().get(musicData.getHashMap_timeRef().get(1)));

                //Button ClickListener
                imageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// if media player
                        if (!hasMediaStarted) { // has not started yet
                            hasMediaStarted = true;
                            if (!mediaPlayer.isPlaying()) {
                                imageBtn.setImageResource(R.drawable.ic_stop);// it needs thread to prepare
                                startSong(musicData.getFile()); // go to set prepare() from prepareAsync at StartSong(lyrics)
                            } else {
                                imageBtn.setImageResource(R.drawable.ic_play);
                                mediaPlayer.pause();
                            }
                        } else { //if already been started
                            if (!mediaPlayer.isPlaying()) { //
                                imageBtn.setImageResource(R.drawable.ic_stop);
                                mediaPlayer.start(); // no Need to prepare to restart over again from the pause
                            } else {
                                imageBtn.setImageResource(R.drawable.ic_play);
                                mediaPlayer.pause();  //just in case of that media player disconnected or completely stopped
                            }
                        }
                    }
                });
            }

            @Override // Error
            public void onFailure(Call<MusicData> call, Throwable throwable) {
                Log.v("textError", "" + throwable.getMessage());
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //Set SeekBar changeListener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) { // User change the SeekBar
//                    Log.v("Seekbar01", "" + seekBar.getMax());
//                    Log.v("Seekbar01", "" + progress);
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }

                //Set the lyrics when any change of seekbar found
                setLyrics();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }//onCreate

    private void setLyrics() {

        //Set Local variable
        Long curPos = Long.parseLong("" + mediaPlayer.getCurrentPosition()); // current position
        ArrayList<Long> hashMap_timeRef = musicData.getHashMap_timeRef(); // starting Time to start of each lyric
        String tem = "";
        Long start_time = 0L;

        // adding 3.5sec to last time to hashMap and its reference (For the Array out of bound)
        Long dummyLast_Time = hashMap_timeRef.get(hashMap_timeRef.size() - 1) + 3500;
        hashMap_timeRef.add(dummyLast_Time);
        musicData.getLyricsMap().put(dummyLast_Time, "");


        //Set lyrics to TextView
        for(int i = 0 ; i < musicData.getLyricsMap().size(); i++){

            start_time = hashMap_timeRef.get(i);
            tem = musicData.getLyricsMap().get(start_time);

            if(curPos >= start_time - 500 &&  curPos <start_time){
                lyrics1.setTextColor(Color.WHITE);
                lyrics2.setTextColor(Color.YELLOW);
            }
            else if (curPos >= start_time  && curPos < start_time+500){
                lyrics1.setText(tem);
                lyrics1.setTextColor(Color.YELLOW);

                lyrics2.setText(musicData.getLyricsMap().get(hashMap_timeRef.get(i + 1)));
                lyrics2.setTextColor(Color.WHITE);
            }
            else if (curPos >= start_time +500 && curPos < hashMap_timeRef.get(i+1)-500) {
                lyrics1.setText(tem);
                lyrics1.setTextColor(Color.YELLOW);

                lyrics2.setText(musicData.getLyricsMap().get(hashMap_timeRef.get(i + 1)));
                lyrics2.setTextColor(Color.WHITE);
            }
        }
    }



    private void startSong(String uri) {
        //set Data to media Player (Must throw exception in case of no file from respond)
        try {
            mediaPlayer.setDataSource(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // prepare process long <- new Thread necessary
        mediaPlayer.prepareAsync(); // prepareAsync() can handle that
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mp.getDuration());
                mediaPlayer.start();
                updateSeekbar();
            }//MediaPlayer onPrepared
        });// MediaPlayer setOnPrepareListener

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                double ratio = percent / 100.0;
                int bufferingLevel = (int) (mp.getDuration() * ratio);
                Log.v("wanttoknow0", "" + ratio);
                Log.v("wanttoknow1", "" + seekBar.getMax());
                Log.v("wanttoknow2", "" + mp.getDuration());
                seekBar.setSecondaryProgress(bufferingLevel);
            }
        });

    }//playSong Method

    private void updateSeekbar() {
        int curPos = mediaPlayer.getCurrentPosition();
        seekBar.setProgress(curPos);
        // set new Thread to process SeekBar
        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();

            }
        };

        // Put them into Looper enqueue by handler (Update UI)
        handler.postDelayed(runnable, 1000);
    }//updateSeekbar()
}//MainActivity Class

