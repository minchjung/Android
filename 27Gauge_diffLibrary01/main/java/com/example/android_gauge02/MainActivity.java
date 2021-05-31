package com.example.android_gauge02;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import androidx.annotation.AnyThread;
import android.view.Display;
import android.util.Log;

import ca.hss.heatmaplib.HeatMap;
import androidx.collection.ArrayMap;
import ca.hss.heatmaplib.HeatMapMarkerCallback;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.SpeedView;

import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private HeatMap map;
    private boolean testAsync = false;
    float scale;
    float meterVal;
    Timer timer;
    AwesomeSpeedometer speedMeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Heatmap
        map = findViewById(R.id.example_map);
        map.setMinimum(0.0);
        map.setMaximum(100.0);
        map.setLeftPadding(100);
        map.setRightPadding(100);
        map.setTopPadding(100);
        map.setBottomPadding(100);
        float w =  getScreenWidth(this);
//        float h = getScreenHeight(this);
        scale = (w/10)/w;
        Log.d("tag",  " scale="+scale);

        map.setRadius(w/12);
        Map<Float, Integer> colors = new ArrayMap<>();
        //build a color gradient in HSV from red at the center to green at the outside
        for (int i = 0; i < 20; i++) {
            float stop = ((float)i) / 20.0f;
            int color = doGradient(i * 5, 0, 100, 0xffee42f4, 0xffff0000);
            colors.put(stop, color);
        }
        map.setColorStops(colors);
        //Timer setting
        timer = new Timer();
        TimerTask adTast =new TimerTask() {
            @Override
            public void run() {
                addData() ;
            }
        };

        //Gauge, Meter
        speedMeter = findViewById(R.id.awesomeSpeedometer);
        speedMeter.setWithTremble(false) ;
        speedMeter.setUnit("pi");

    }

    public float getScreenWidth(Activity MainActivity) {
        Display display = MainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size.x;
    }
    public float getScreenHeight(Activity MainActivity) {
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
        float gap = scale/2;
        map.clearData();
        Random rand = new Random();
        //add 20 random points of random intensity
        for (int i = 0; i < 10; i++) {
            for(int j = 0 ; j<10; j++){
                HeatMap.DataPoint point = new HeatMap.DataPoint(scale +scale*j,  gap+scale*i, clamp(rand.nextDouble(), 0.0, 100.0));
                map.addData(point);
            }
        }
        meterVal =  rand.nextFloat();
        speedMeter.speedTo(meterVal,500);
    }

    @SuppressWarnings("SameParameterValue")
    private double clamp(double value, double min, double max) {
        return value * (max - min) + min;
    }

    @SuppressWarnings("SameParameterValue")
    private static int doGradient(double value, double min, double max, int min_color, int max_color) {
        if (value >= max) return max_color;
        if (value <= min) return min_color;

        float[] hsvmin = new float[3];
        float[] hsvmax = new float[3];
        float frac = (float)((value - min) / (max - min));
        Color.RGBToHSV(Color.red(min_color), Color.green(min_color), Color.blue(min_color), hsvmin);
        Color.RGBToHSV(Color.red(max_color), Color.green(max_color), Color.blue(max_color), hsvmax);

        float[] retval = new float[3];
        for (int i = 0; i < 3; i++)
            retval[i] = interpolate(hsvmin[i], hsvmax[i], frac);

        return Color.HSVToColor(retval);
    }

    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }


}