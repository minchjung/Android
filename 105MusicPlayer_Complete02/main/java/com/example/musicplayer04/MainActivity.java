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
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//glide library (출저:https://github.com/bumptech/glide)
//retrofit2 library  (출저: https://github.com/square/retrofit)

/*remark
    On Create Life Cycle
    1. Initialize All views, fields, class-object in the package,
    2. Http communication by retrofit2
       --(when retrofit2 gets Json data from the response, Set the values to appropriate MusicData field )
    3. When response Success
      a) UI update for initial page with response Data
      b) Button on Click Listener
      c) MediaPlayer prepare(),start(),pause() on Button on ClickListener
 */
/*remark
   Other life Cycle than the MainActivity
   1. MediaPlayer prepare and start by prepareAsync()->onPrepareListener()->onPrepare()
   2. Update Seekbar by updateSeekbar()
   3.
*/
public class MainActivity extends AppCompatActivity {

    private TextView singer, title, album;
    private TextView lyrics1, lyrics2, totTime, currTime;
    private ImageView albumImg;
    private ImageButton imageBtn;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;

    MusicData musicData;
    Runnable runnable;
    Handler handler;
    private boolean hasMediaStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get this activity context for Glide using in different Thread
        Context actContext = this;

        //remark  [Main-1. initializing]
        //Initialize view
        singer = findViewById(R.id.singer);
        title = findViewById(R.id.title);
        album = findViewById(R.id.album);
        albumImg = findViewById(R.id.albumImg);
        lyrics1 = findViewById(R.id.lyrics1);
        lyrics2 = findViewById(R.id.lyrics2);
        totTime = findViewById(R.id.totTime);
        currTime = findViewById(R.id.currTime);
        imageBtn = findViewById(R.id.imageBtn);

        //Initialize seekBar  and MediaPlayer
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //Initialize handler
        handler = new Handler();

        //Initialize retrofit2
        Api_interface myAPIService = RetrofitClientInstance.getRetrofitInstance().create(Api_interface.class);

        //remark [Main-2. Http communication by retrofit2]
        //Request and Get Response(Json) && set Data
        Call<MusicData> call = myAPIService.getMusicData();
        call.enqueue(new Callback<MusicData>() { //results into enqueue

            //remark  [ Main-3.When response Success]
            @Override // Success - 1. set Init of UI,   2. set MediaPlayer from Button Click    3. set SeeckBar
            public void onResponse(Call<MusicData> call, Response<MusicData> response) {
                // Initialize MusicData on Response
                musicData = response.body();
                // Initial UI update from the response (json data )
                title.setText(musicData.getTitle()); //song title
                singer.setText(musicData.getSinger()); //singer
                album.setTypeface(null, Typeface.ITALIC); // font italic
                album.setText(musicData.getAlbum()); // for album name
                Glide.with(actContext).load(musicData.getImage()).into(albumImg); // album image

                // Set for the Lyrics Parsing
                musicData.setLyricsMap(musicData.getLyrics());
                lyrics2.setText(musicData.getLyricsMap().get(musicData.getHashMap_timeRef().get(0)));
//                Log.v("lyrics", musicData.getLyrics());

                //Button ClickListener
                imageBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {// if media player
                        if (!hasMediaStarted) { // has not started yet
                            hasMediaStarted = true; //no need to set any process for change this to false
                                                    // (since we only want to use a single MediaPlayer )
                            imageBtn.setImageResource(R.drawable.ic_pause);
                            startSong(musicData.getFile()); // it needs thread to prepare
                            // go to set MediaPlayer.prepare() from prepareAsync at method [StartSong(lyrics)]

                        }
                        else { //if already been started
                            if (!mediaPlayer.isPlaying()) { //
                                imageBtn.setImageResource(R.drawable.ic_pause);
                                mediaPlayer.start(); // no Need to prepare to restart over again from the pause
                            } else {
                                imageBtn.setImageResource(R.drawable.ic_play);
                                mediaPlayer.pause();  // While MediaPlayer playing(or has not stopped, or not null ),
                                // pause<->start good to back and forth on the same thread
                            }
                        }
                    }
                });
            }//onResponse()

            @Override // Error
            public void onFailure(Call<MusicData> call, Throwable throwable) {
                Log.v("textError", "" + throwable.getMessage());
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //Callback

        //Set SeekBar changeListener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("seekBarChange", "progress=" + progress);

                if (fromUser) { // User change the SeekBar
//                    Log.v("Seekbar01", "" + progress);
                    mediaPlayer.seekTo(progress);
                    updateSeekbar(); //  for the UI update[ new Thread with demon-handler]
                }
                setLyrics(); //Set the lyrics on any change of seekbar
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }//onCreate


    private void setLyrics() {

        //Set Local variable
        Long curPos = Long.parseLong("" + mediaPlayer.getCurrentPosition()); // current position
        ArrayList<Long> hashMap_timeRef = musicData.getHashMap_timeRef(); // starting Time for each lyrics

        String tem = "";
        Long start_time = 0L;
        Long swapInterval = 500L; //Switching interval mm sec

        //Set HashMap and ArrayList(<-contains key to access hashMap)
        // adding 3.5sec to last time to hashMap and its reference (For the Array out of bound)
        Long dummyLast_Time = hashMap_timeRef.get(hashMap_timeRef.size() - 1) + 3500;
        hashMap_timeRef.add(dummyLast_Time);
        musicData.getLyricsMap().put(dummyLast_Time, ""); // HashMap(key=starting time : value= lyrics )


        //Set lyrics to TextView (switching them 0.5sec before displaying)
        for(int i = 0 ; i < musicData.getLyricsMap().size(); i++){

            start_time = hashMap_timeRef.get(i);
            tem = musicData.getLyricsMap().get(start_time);
            // switching Colors (0.5sec ahead to switch lyrics  )
            if(curPos >= start_time - swapInterval &&  curPos <start_time){
                lyrics1.setTextColor(Color.WHITE);
                lyrics2.setTextColor(Color.rgb(255,235,60));

            } //switching lyrics and colors
            else if (curPos >= start_time  && curPos <hashMap_timeRef.get(i+1)- swapInterval ) {
                lyrics1.setText(tem);
                lyrics1.setTextColor(Color.rgb(255, 235, 60));

                lyrics2.setText(musicData.getLyricsMap().get(hashMap_timeRef.get(i + 1)));
                lyrics2.setTextColor(Color.WHITE);
            }
        }
    }// setLyrics()

    //Set MediaPlayer [MUST!! onPrepare with prepareAsync]
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
                updateSeekbar(); // After mediaPlayer started,
                                 // Update the SeekBar value in different thread

            }//MediaPlayer onPrepared
        });// MediaPlayer setOnPrepareListener

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                double ratio = percent / 100.0;
                int bufferingLevel = (int) (mp.getDuration() * ratio);
//                Log.v("wanttoknow2", "" + mp.getDuration());
                seekBar.setSecondaryProgress(bufferingLevel);
            }
        });
    }//startSong()

    // UI update if any change from  seekBar by User or by mediaPlayer
    // new Thread for background data process,
    // single demon for UI update, that on Main thread
    private void updateSeekbar() {

        // local variable for current and total progress
        int curPos =mediaPlayer.getCurrentPosition();
        int totDur = mediaPlayer.getDuration();

        // seekBar update
        seekBar.setProgress(curPos);

        // time update - needs String parsing
        currTime.setText(
                updatePLay_time( (int) TimeUnit.MILLISECONDS.toMinutes(curPos) )  + ":"
              + updatePLay_time( (int)( TimeUnit.MILLISECONDS.toSeconds(curPos)%60 ) )+" / ");
        totTime.setText(
                updatePLay_time( (int) TimeUnit.MILLISECONDS.toMinutes(totDur) ) + ":"
              + updatePLay_time( (int)( TimeUnit.MILLISECONDS.toSeconds(totDur)%60 ) ));

        // set new Thread to process SeekBar
        if(!mediaPlayer.isPlaying()){ imageBtn.setImageResource(R.drawable.ic_play); }
        else{
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekbar(); //callback
                } //completely stop this reclusive process only if music stop playing
            };
        }
        handler.postDelayed(runnable, 1000);
        // Update UI -Put them into Looper enqueue by handler( created on main thread)
        // delayed post  so thereby, last call back can also handle even if music is not playing

    }//updateSeekbar()

    // Set to display playing time (ex, 1:31 -> 01:31)
    private String updatePLay_time( int dur){

        String strDur="";
        if(dur/10==0)   strDur = "0" + dur;
        else            strDur = ""  + dur;

        return strDur;
    }

}//MainActivity Class
