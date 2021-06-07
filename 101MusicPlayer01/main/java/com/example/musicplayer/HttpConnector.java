package com.example.musicplayer;

import android.util.Log;

import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpConnector {
    
    private String strURL="";
    private HttpURLConnection conn = null;
    HashMap<String, String> jsonMap = new HashMap<>(); // Set HashMap to put [key : value]-json Data

    public HttpConnector(String url)  { // mainActivity Constructor DI
        this.strURL = url;
    }

    public void setJsonReader() {
        try {
            URL url  = new URL(strURL); // Set URL object from the field (constructor DI)
            conn = (HttpURLConnection) url.openConnection(); // set HTTP Communication via url
            if (conn != null) {
                conn.setRequestMethod("GET"); // set request method GET
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){ // response 200
                    JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream(),"UTF-8")); //set JsonReader to parse json Data
                     // call the function to parse && give the data to handler
                    Log.v("runThread", "왔따요");
                    jsonParser(reader);
                } conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // json Parsing
    public void jsonParser(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();// key
            if (name.equals("singer")) jsonMap.put("singer", reader.nextString()); // put key, value
            else if (name.equals("album")) jsonMap.put("album", reader.nextString());// put key, value
            else if (name.equals("title")) jsonMap.put("title", reader.nextString());// put key, value
            else if (name.equals("duration")) jsonMap.put("duration", reader.nextString());// put key, value
            else if (name.equals("image")) jsonMap.put("image", reader.nextString());// put key, value
            else if (name.equals("file")) jsonMap.put("file", reader.nextString());// put key, value
            else if (name.equals("lyrics")) jsonMap.put("lyrics", reader.nextString());// put key, value
            else reader.skipValue();
            Log.v("jsonParser",jsonMap.get("singer"));
        }
        reader.endObject();
    }

}
