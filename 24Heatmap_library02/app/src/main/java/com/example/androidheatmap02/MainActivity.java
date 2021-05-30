package com.example.androidheatmap02;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import androidx.annotation.AnyThread;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Map;
import java.util.Random;

import androidx.collection.ArrayMap;
import ca.hss.heatmaplib.HeatMap;
import ca.hss.heatmaplib.HeatMapMarkerCallback;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private HeatMap map;
    private boolean testAsync = false;
    float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckBox box = findViewById(R.id.change_async_status);
        box.setOnCheckedChangeListener(this);
        float w =  getScreenSize(this);
        float h = getScreenSize2(this);
        scale = (w/10)/w;

        Log.d("tag",  " scale="+scale);

        map = findViewById(R.id.example_map);
        map.setMinimum(0.0);
        map.setMaximum(100.0);
        map.setLeftPadding(100);
        map.setRightPadding(100);
        map.setTopPadding(100);
        map.setBottomPadding(100);
//        map.setMarkerCallback(new HeatMapMarkerCallback.CircleHeatMapMarker(0xff9400D3));
        map.setRadius(w/12);
        Map<Float, Integer> colors = new ArrayMap<>();
        //build a color gradient in HSV from red at the center to green at the outside
        for (int i = 0; i < 21; i++) {
            float stop = ((float)i) / 20.0f;
            int color = doGradient(i * 5, 0, 100, 0xffee42f4, 0xffff0000);
            colors.put(stop, color);
        }
        map.setColorStops(colors);

        map.setOnMapClickListener(new HeatMap.OnMapClickListener() {
            @Override
            public void onMapClicked(int x, int y, HeatMap.DataPoint closest) {
                addData();
            }
        });


    } // onCreatea
    public float getScreenSize(Activity MainActivity) {
        Display display = MainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size.x;
    }
    public float getScreenSize2(Activity MainActivity) {
        Display display = MainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size.y;
    }
    private void addData() {
        if (testAsync) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    drawNewMap();
                    map.forceRefreshOnWorkerThread();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            map.invalidate();
                        }
                    });
                }
            });
        }
        else {
            drawNewMap();
            map.forceRefresh();
        }
    }

    @AnyThread
    private void drawNewMap() {
        float gap = scale +scale/2;
        map.clearData();
        Random rand = new Random();
        //add 20 random points of random intensity
        for (int i = 0; i < 10; i++) {
            for(int j = 0 ; j<10; j++){
                if(i==0 && j==0) gap = scale ;
                HeatMap.DataPoint point = new HeatMap.DataPoint(scale +scale*j,  gap+scale*i, clamp(rand.nextDouble(), 0.0, 100.0));
                map.addData(point);

                Log.v("tag2", "x=" + scale*j + " y=" + scale*i);
            }
        }
    }


    @SuppressWarnings("SameParameterValue")
    private double clamp(double value, double min, double max) {
        return value * (max - min) + min;
    }

    @SuppressWarnings("SameParameterValue")
    private static int doGradient(double value, double min, double max, int min_color, int max_color) {
        if (value >= max) {
            return max_color;
        }
        if (value <= min) {
            return min_color;
        }
        float[] hsvmin = new float[3];
        float[] hsvmax = new float[3];
        float frac = (float)((value - min) / (max - min));
        Color.RGBToHSV(Color.red(min_color), Color.green(min_color), Color.blue(min_color), hsvmin);
        Color.RGBToHSV(Color.red(max_color), Color.green(max_color), Color.blue(max_color), hsvmax);
        float[] retval = new float[3];
        for (int i = 0; i < 3; i++) {
            retval[i] = interpolate(hsvmin[i], hsvmax[i], frac);
        }
        return Color.HSVToColor(retval);
    }

    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        testAsync = !testAsync;
    }




}