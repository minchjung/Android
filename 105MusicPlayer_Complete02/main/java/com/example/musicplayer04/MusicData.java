package com.example.musicplayer04;
import android.net.Uri;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//Json mapping object when it gets response return each value to the key (Here field name = json key name =mapping key name )
public class MusicData {

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

    private HashMap<Long, String> lyricsMap;
    private ArrayList <Long> hashMap_timeRef;
//    private Long[] intervalDur;

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


    //Setter
    //Set Lyrics HashMap (key=ms : value=lyrics) && ArrayList for the index Reference
    public void setLyricsMap(String lyrics) {
        this.lyricsMap = new HashMap<>();
        this.hashMap_timeRef = new ArrayList<>();
//        this.intervalDur = new Long[hashMap_timeRef.size()+1];

        for (String line :lyrics.split("\\n")) {
            Long dur=0l;
            String[] numberLine = line.substring(1, line.indexOf("]")).split(":");
            dur += TimeUnit.MINUTES.toMillis(Long.parseLong(numberLine[0])) + TimeUnit.SECONDS.toMillis(Long.parseLong(numberLine[1])) + Long.parseLong(numberLine[2]);

            hashMap_timeRef.add(dur);
            lyricsMap.put(dur, line.substring(line.indexOf("]")+1));

//            Log.v("DurTime00","line[0]="+numberLine[0]+",  line[1]=" + numberLine[1] +",  line[2]="+numberLine[2]);
//            Log.v("DurTime01","dur="+dur+",  line="+line.substring(line.indexOf("]")+1));
        }

//        for (int i = 0; i < hashMap_timeRef.size()-1; i++)
//            intervalDur[i] = hashMap_timeRef.get(i + 1) - hashMap_timeRef.get(i);

    }
}
