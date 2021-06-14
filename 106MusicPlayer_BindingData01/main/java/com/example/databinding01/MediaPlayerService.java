package com.example.databinding01;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import com.example.databinding01.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private MusicData musicData;
    private boolean hasMediaStarted = false;
    Notification mNotification;
    private String CHANNEL_ID = "CHANNEL_1";
    private ArrayList<Long> timeRef;

    //binder to activity
    Handler handler, handler2;
    Runnable runnable, runnable2;

    private final IBinder mBinder = new LocalBinder();


    //Constructor MUST EMPTY!!
    public MediaPlayerService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        handler = new Handler(Looper.getMainLooper());
        handler2 = new Handler(Looper.getMainLooper());

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MUSIC PLAYER", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MUSIC SERVICE")
                .setContentText("MUSIC PLAYER IS RUNNING")
                .setSmallIcon(R.drawable.ic_play).build();
        startForeground(2,notification);

//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mNotification =
//                    new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
//                            .setContentTitle("API Service")
//                            .setContentText("Service is running")
//                            .setSmallIcon(R.mipmap.ic_launcher_round)
//                            .setContentIntent(pendingIntent)
//                            .setTicker("API")
//                            .build();
//        } else {
//            mNotification =
//                    new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                            .setContentTitle("API Service")
//                            .setContentText("Service is running")
//                            .setSmallIcon(R.mipmap.ic_launcher_round)
//                            .setSound(null)
//                            .setContentIntent(pendingIntent)
//                            .setTicker("API")
//                            .build();
//        }
        super.onCreate();
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void startMyOwnForeground() {
//        String channelName = "My Background Service";
//        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return Service.START_STICKY;
        else{

            initOnClickListener();
            playMedia(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initOnClickListener() {
        MainActivity.mainBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
                updateSeekbar(); //  for the UI update[ new Thread with demon-handler]
                setLyrics(); //Set the lyrics on any change of seekbar
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        MainActivity.mainBinding.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasMediaStarted) { // has not started yet
                    hasMediaStarted = true; //no need to set any process for change this to false
                    // (since we only want to use a single MediaPlayer )
                    MainActivity.mainBinding.imageBtn.setImageResource(R.drawable.ic_pause);

                } else { //if already been started
                    if (!mediaPlayer.isPlaying()) { //
                        MainActivity.mainBinding.imageBtn.setImageResource(R.drawable.ic_pause);
                        mediaPlayer.start(); // no Need to prepare to restart over again from the pause
                    } else {
                        MainActivity.mainBinding.imageBtn.setImageResource(R.drawable.ic_play);
                        mediaPlayer.pause();  // While MediaPlayer playing(or has not stopped, or not null ),
                        // pause<->start good to back and forth on the same thread
                    }
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        stopForeground(true);
        super.onDestroy();
    }


    private void playMedia(Intent intent) {
        if (mediaPlayer != null) return;

        this.musicData = (MusicData) intent.getSerializableExtra("musicData");
        this.timeRef = musicData.getHashMap_timeRef();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(intent.getStringExtra("mediaURL"));
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        MainActivity.mainBinding.seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar();

    }


    private void setLyrics() {

        //Set Local variable
        Long curPos = Long.parseLong("" + mediaPlayer.getCurrentPosition()); // current position
        this.timeRef = musicData.getHashMap_timeRef(); // starting Time for each lyrics
        Log.v("curPos", "" + curPos);
        String tem = "";
        Long start_time = 0L;
        Long swapInterval = 500L; //Switching interval mm sec

        //Set HashMap and ArrayList(<-contains key to access hashMap)
        // adding 3.5sec to last time to hashMap and its reference (For the Array out of bound)
        Long dummyLast_Time = timeRef.get(timeRef.size() - 1) + 3500;
        timeRef.add(dummyLast_Time);
        musicData.getLyricsMap().put(dummyLast_Time, ""); // HashMap(key=starting time : value= lyrics )


        //Set lyrics to TextView (switching them 0.5sec before displaying)
        for (int i = 0; i < musicData.getLyricsMap().size(); i++) {

            start_time = timeRef.get(i);
            tem = musicData.getLyricsMap().get(start_time);
            // switching Colors (0.5sec ahead to switch lyrics  )
            if (curPos >= start_time - swapInterval && curPos < start_time) {
                MainActivity.mainBinding.lyrics1.setTextColor(Color.WHITE);
                MainActivity.mainBinding.lyrics2.setTextColor(Color.rgb(255, 235, 60));

            } //switching lyrics and colors
            else if (curPos >= start_time && curPos < timeRef.get(i + 1) - swapInterval) {
                MainActivity.mainBinding.lyrics1.setText(tem);
                MainActivity.mainBinding.lyrics1.setTextColor(Color.rgb(255, 235, 60));

                MainActivity.mainBinding.lyrics2.setText(musicData.getLyricsMap().get(timeRef.get(i + 1)));
                MainActivity.mainBinding.lyrics2.setTextColor(Color.WHITE);
            }
        }
        // set new Thread to process SeekBar
        runnable2 = new Runnable() {
            @Override
            public void run() {
                setLyrics(); //callback
            } //completely stop this reclusive process only if music stop playing
        };
        handler2.postDelayed(runnable2, 500);
    }// setLyrics()


    // UI update if any change from  seekBar by User or by mediaPlayer
    // new Thread for background data process,
    // single demon for UI update, that on Main thread
    private void updateSeekbar() {

        // local variable for current and total progress
        int curPos = mediaPlayer.getCurrentPosition();
        int totDur = mediaPlayer.getDuration();

        // seekBar update
        MainActivity.mainBinding.seekBar.setProgress(curPos);

        // time update - needs String parsing
        MainActivity.mainBinding.currTime.setText(
                updatePLay_time((int) TimeUnit.MILLISECONDS.toMinutes(curPos)) + ":"
                        + updatePLay_time((int) (TimeUnit.MILLISECONDS.toSeconds(curPos) % 60)) + " / ");
        MainActivity.mainBinding.totTime.setText(
                updatePLay_time((int) TimeUnit.MILLISECONDS.toMinutes(totDur)) + ":"
                        + updatePLay_time((int) (TimeUnit.MILLISECONDS.toSeconds(totDur) % 60)));
        // set new Thread to process SeekBar
        if (!mediaPlayer.isPlaying()) {
            MainActivity.mainBinding.imageBtn.setImageResource(R.drawable.ic_play);
            return;
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekbar(); //callback
                } //completely stop this reclusive process only if music stop playing
            };
        }
        handler.postDelayed(runnable, 500);
        // Update UI -Put them into Looper enqueue by handler( created on main thread)
        // delayed post  so thereby, last call back can also handle even if music is not playing

    }//updateSeekbar()


    // Set to display playing time (ex, 1:31 -> 01:31)
    private String updatePLay_time(int dur) {

        String strDur = "";
        if (dur / 10 == 0) strDur = "0" + dur;
        else strDur = "" + dur;

        return strDur;
    }


    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}