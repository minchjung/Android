package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fade(View view) {
        ImageView firstImg = findViewById(R.id.firstImg);
        ImageView firstNum = findViewById(R.id.firstNum);

        firstImg.animate().alpha(0f).setDuration(2000);
        firstNum.animate().alpha(1f).setDuration(2000);
    }
}