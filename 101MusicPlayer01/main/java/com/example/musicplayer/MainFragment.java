package com.example.musicplayer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.musicplayer.databinding.FragmentMainBinding;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        binding = FragmentMainBinding.bind(view);

        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpConnector httpConnector = new HttpConnector("https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json");
                httpConnector.setJsonReader();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.singer.setText(httpConnector.jsonMap.get("singer"));
                    }
                });
            }
        });

        return binding.getRoot();

    }//onCreateView
}