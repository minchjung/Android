package com.example.chart;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LineChart lineChart;
    int colorArray[] = {R.color.color1,R.color.color2,R.color.color3,R.color.color4,R.color.color5,R.color.color6,R.color.color7,R.color.color8};
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

        // Setting the background and NoData Text
//        lineChart.setBackgroundColor(Color.GREEN);
        lineChart.setNoDataText("No Data");
        lineChart.setNoDataTextColor(Color.BLUE);

        // Setting the GridBackground
        lineChart.setDrawGridBackground(true);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.RED);
//        lineChart.setBorderWidth(5);

        
        //lineData setting
        lineDataSet1.setLineWidth(4);
        lineDataSet1.setColor(Color.RED);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setDrawCircleHole(false);
//        lineDataSet1.enableDashedLine(5,10,0);
        lineDataSet1.setColors(colorArray,MainActivity.this);

        // Setting the description
        Description description = new Description();
        description.setText("Pressure Best");
        description.setTextColor(Color.BLUE);
        description.setTextSize(20);
        lineChart.setDescription(description);

        // Setting the legend
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.RED);
        legend.setTextSize(25);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(20);
        legend.setXEntrySpace(15);
        legend.setFormToTextSpace(10);
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