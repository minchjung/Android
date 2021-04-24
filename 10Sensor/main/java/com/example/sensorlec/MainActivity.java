package com.example.sensorlec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainAcitivity";
    private SensorManager sensorManager;
    Sensor accelerometer ;

    TextView xValue,yValue,zValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = findViewById(R.id.xValue);
        yValue = findViewById(R.id.yValue);
        zValue = findViewById(R.id.zValue);

        Log.d(TAG, "onCreate:Initializing Sensor Services") ;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG,"onCreate: Registered accelerameter listner");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "OnSensorChanged: X:"+ event.values[0] +"Y:" +event.values[1] +"Z:"+event.values[2]);
        xValue.setText("xValue: " + event.values[0]);
        yValue.setText("xValue: " + event.values[1]);
        zValue.setText("xValue: " + event.values[2]);
    }
}