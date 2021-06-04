package com.example.threadfragment03;

import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.threadfragment03.databinding.FragmentMainBinding;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ca.hss.heatmaplib.HeatMap;

public class MainFragment extends Fragment {
    View view;
    private MainViewModel viewModel;
    private FragmentMainBinding binding;

    private boolean testAsync = false;
    private HeatMap heatMap;
    private Map<Float, Integer> colors = new ArrayMap<>();
    private Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        binding = FragmentMainBinding.bind(view);

        timer = new Timer();
        heatMap = binding.heatmap;
        heatMap.setMinimum(0.0);
        heatMap.setMaximum(255.0);
        for (int i = 0; i < 20; i++) {
            float stop = ((float) i) / 20.0f;
            int color = doGradient(i * 5, 0, 100, 0xffee42f4, 0xffff0000);;
            colors.put(stop, color);
        }
        heatMap.setColorStops(colors);


        return binding.getRoot();
    }

    @Override //  화면 작업 완료
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(MainViewModel.class);
        viewModel.progressLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> progress) {
                binding.prog1.setProgress(progress.get(100));
                binding.prog2.setProgress(progress.get(101));
                binding.prog3.setProgress(progress.get(102));
                binding.gauge.setProgress(progress.get(103));
                binding.textView1.setText(""+progress.get(100)+"pi");
                binding.textView2.setText(""+progress.get(101)+"pi");
                binding.textView3.setText(""+progress.get(102)+"pi");

                float h = view.getHeight()/2;
                float scale = (h/10)/h;

                heatMap.setRadius(h/12);
                heatMap.clearData();

                int idx = 0;
                float gap = 0;
                for (int i = 0; i < 10; i++) {
                    for(int j = 0 ; j<10; j++){
                        if(i==0 && j==0) gap = scale*5 ;
                        else gap = 0 ;
                        HeatMap.DataPoint point = new HeatMap.DataPoint(scale +scale*j,  gap+ scale*i,
                                clamp((double)(progress.get(idx)/100.0), 0.0, 255.0)
                        );
                        heatMap.addData(point);
                        idx++;
                    }
                }
               heatMap.forceRefresh();
            }
        });
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewModel.longTask();
            }
        };
        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.schedule(timerTask, 0, 30);
            }
        });
        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
            }
        });

    }//OnViewCreated


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
}//MainFragment