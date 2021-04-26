package com.example.chart;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LineChart lineChart;
    Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // LineChart object
        lineChart = findViewById(R.id.linechart);
        // Call the data , put into the DataSet obj
        LineDataSet lineDataSet1 = new LineDataSet(dataValue(),"Pressure 1");
        LineDataSet lineDataSet2 = new LineDataSet(dataValue(),"Pressure 2");
        // ILineDataSet obj in ArrayList as to put the data in the DataSet object
        ArrayList<ILineDataSet> dataSets =new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        //LineData obj, putting the ILineDataSet
        LineData data = new LineData(dataSets);

        lineChart.setBackgroundColor(Color.GREEN);
        lineChart.setNoDataText("No Data");
        lineChart.setNoDataTextColor(Color.BLUE);

        lineChart.setDrawGridBackground(true);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.RED);
//        lineChart.setBorderWidth(5);

        Description description = new Description();
        description.setText("Pressure Best");
        description.setTextColor(Color.BLUE);
        description.setTextSize(20);
        lineChart.setDescription(description);


        //execute it
        lineChart.setData(data);
        lineChart.invalidate();
    }
    private ArrayList<Entry>dataValue(){
        ArrayList<Entry> dataVals =new ArrayList<>();
        for(int i =0; i <16; i++)
            dataVals.add(new Entry(i,random.nextInt(25000)));
        return dataVals;
    }
}