package com.example.databinding01;

import android.app.Service;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MusicData extends BaseObservable implements Serializable {

    @SerializedName("singer")//json mapping
    @Expose
    private String singer;

    @SerializedName("title")//json mapping
    @Expose
    private String title;

    @SerializedName("album")//json mapping
    @Expose
    private String album;

    @SerializedName("lyrics")//json mapping
    @Expose
    private String lyrics;

    @SerializedName("image")//json mapping
    @Expose
    private String image;

    @SerializedName("file")//json mapping
    @Expose
    private String file;

    private int curPos;
    private int totDur;

    private HashMap<Long, String> lyricsMap;
    private ArrayList<Long> hashMap_timeRef;
    private ArrayList<String> lyricsList;


    public void setCurPos(int curPos) {
        this.curPos = curPos;
        notifyPropertyChanged(BR.curPos);
    }

    public void setTotDur(int totDur) {
        this.totDur = totDur;
        notifyPropertyChanged(BR.totDur);
    }
    @Bindable
    public int getCurPos() {
        return curPos;
    }
    @Bindable
    public int getTotDur() {
        return totDur;
    }

    //Constructor DI
    //Set all json value to each key(field) when initialize
    public MusicData(String singer, String title, String album, String lyrics, String image, String file) {
        this.singer = singer;
        this.title = title;
        this.album = album;
        this.lyrics = lyrics;
        this.image = image;
        this.file = file;

    }
    //Getter
    public String getSinger(){ return singer; }

    public String getTitle() { return title;}

    public String getAlbum() { return album;}

    public String getLyrics() {return lyrics;}

    public String getImage() { return image;}

    public String getFile() { return file;}

    public HashMap<Long, String> getLyricsMap(){ return this.lyricsMap;}

    public ArrayList<Long> getHashMap_timeRef() { return hashMap_timeRef;}

    public ArrayList<String> getLyricsList() { return lyricsList; }

    //Setter
    //Set Lyrics HashMap (key=ms : value=lyrics) && ArrayList for the index Reference
    public void setLyricsMap(String lyrics) {

        this.lyricsMap = new HashMap<>();
        this.hashMap_timeRef = new ArrayList<>();
        this.lyricsList = new ArrayList<>();

        for (String line : lyrics.split("\\n")) {
            Long dur = 0l;
            String[] numberLine = line.substring(1, line.indexOf("]")).split(":");
            dur += TimeUnit.MINUTES.toMillis(Long.parseLong(numberLine[0])) + TimeUnit.SECONDS.toMillis(Long.parseLong(numberLine[1])) + Long.parseLong(numberLine[2]);

            hashMap_timeRef.add(dur);
            lyricsMap.put(dur, line.substring(line.indexOf("]") + 1));
            lyricsList.add(line.substring(line.indexOf("]") + 1));
        }

    }
}
