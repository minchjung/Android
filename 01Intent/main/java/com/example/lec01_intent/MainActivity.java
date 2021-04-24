package com.example.lec01_intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    static final String extra_message = "intent_Massage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void sendMassage(View view) {
        Intent intent = new Intent(this, DisplayActivity.class );

        EditText editText = findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        intent.putExtra(extra_message, message);
        startActivity(intent);
    }
}