package com.example.barchart;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Random random = new Random();
    BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // view connect
        barChart = findViewById(R.id.barChart);
        // Set dataSet
        BarDataSet barDataSet = new BarDataSet(dataVal(), "Pressure1");
        BarDataSet barDataSet2 = new BarDataSet(dataVal(), "Pressure1");

        // Before add to the chart, set layout chart, color, text , etc ..
        barDataSet.setColor(Color.RED);
        barDataSet2.setColor(Color.BLUE);

        // Add the DataSets to Data  & put it into the barChart
        BarData barData = new BarData(barDataSet,barDataSet2);
        barChart.setData(barData);

    }
    private ArrayList<BarEntry>dataVal(){
        ArrayList<BarEntry> arr = new ArrayList<>();
        for(int i =0; i < 16 ; i ++)
            arr.add(new BarEntry(i,random.nextInt(250001)));

        return arr ;
    }

}