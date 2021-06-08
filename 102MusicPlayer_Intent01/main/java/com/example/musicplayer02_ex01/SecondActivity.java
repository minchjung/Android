package com.example.musicplayer02_ex01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView messageTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");

        messageTxt = findViewById(R.id.messageTxt);
        messageTxt.setText(age + " " + name);
        
        //Return the result to MainActivity 
        findViewById(R.id.resultBtn).setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", messageTxt.getText().toString());

        setResult(RESULT_OK, resultIntent);
        finish(); // SecondActivity Close
    }
}