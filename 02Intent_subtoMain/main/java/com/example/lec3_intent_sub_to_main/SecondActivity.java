package com.example.lec3_intent_sub_to_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    static String resultMessage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");

        resultMessage = age + "살 쳐묵한" + "  " + name + " 이라고 합니다." ;
        TextView textView = findViewById(R.id.result_text);
        textView.setText( resultMessage);

        findViewById(R.id.btn_result).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("result", resultMessage);
        setResult(RESULT_OK,intent);

        finish();
    }
}