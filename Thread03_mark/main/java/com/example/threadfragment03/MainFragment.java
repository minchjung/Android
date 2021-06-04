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

    @Override //Background onCreate View
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false); // Initialize of view (-Set and Link to the Fragment-layout)
        binding = FragmentMainBinding.bind(view); // This object (FagmentMainBinding <-Generated automatically when view sets on the fragment layout)
        // MainActivity onCreate 와 유사하게 Fragment Initialize 할때, view 객체에 필요한 field 를 setting 한다고 생각 되어짐 (not 100% sure but it works)
        timer = new Timer();
        heatMap = binding.heatmap; // layout을 inflate한 view객체를 binding한 Fragment <-- 내가 정의한 MainFragment를 상속하면서 view객체와  binding 된것

        //heatMap 필요값 설정
        heatMap.setMinimum(0.0);
        heatMap.setMaximum(255.0);
        for (int i = 0; i < 20; i++) {
            float stop = ((float) i) / 20.0f;
            int color = doGradient(i * 5, 0, 100, 0xffee42f4, 0xffff0000);;
            colors.put(stop, color);
        }
        heatMap.setColorStops(colors);


        return binding.getRoot(); //<-- 최상위 view 를 return함 (Main에서 최상위 view, 즉 모든 fragment view 속성을 return 받게됨 )
    }

    @Override //  화면 작업 완료
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(MainViewModel.class); // <--AndroidViewModel 을 상속받은 MainViewModel 객체
        // (MainViewModel: Thread 에서 작업을 수행할 구체 클래스인 ThreadRepo에 필요한  의존성을 주입 및
        //  성공시 넘어오는 결과 데이터를 오버라이드해 return 받는 객체 )
        // Thread Constructor 의존성 연결, 쓰레드 처리 후 결과물을 넘겨 받는 가교 역할.
        //  onCreate 주기에서 App 과 Activity를 찾아서 ViewModel 을 연결해 주는 작업을 해주지만,
        // 위 작업은 수작업 코드로 해주고 있음

        viewModel.progressLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Integer>>() {
            //viewModel 의 field 인 progressLiveData 는 안드로이드에서 제공하는 라이브데이터 라이브러리를 dependency 추가해
            // view 객체에서 동적으로 변하는 View 또는 data 를 Observe 해서, Observe 쉽게 해당 객체나 데이터를 쉽게 받아 올 수 있게 도와주는 라이브러리 (라고 이해)
            @Override // 여기가 화면 작업. 즉 실질적인 UI update가 일어나는 곳 but 그전에 Handler 에서 먼저 처리됨
            public void onChanged(ArrayList<Integer> progress) { //<-- Thread 처리 데이터를 ArrayList로 받아옴
                binding.prog1.setProgress(progress.get(100));// gauge 및 progressBar 값 할당 !! => UI Update
                binding.prog2.setProgress(progress.get(101));
                binding.prog3.setProgress(progress.get(102));
                binding.gauge.setProgress(progress.get(103));
                binding.textView1.setText(""+progress.get(100)+"pi");
                binding.textView2.setText(""+progress.get(101)+"pi");
                binding.textView3.setText(""+progress.get(102)+"pi");
                //HeatMap 값 할당 !
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
        //Timer => another Thread
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewModel.longTask();
            }
        }; // Start
        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.schedule(timerTask, 0, 30);
            }
        });// Stop
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