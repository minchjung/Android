package com.example.lec01_intent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        // [intent]
        Intent intent = getIntent();
        // intent approach to the other activity key value to get the String text
        String message =  intent.getStringExtra(MainActivity.extra_message);
        // if not null, method operates
        //(in the same package, it can approach to the static member value by className.member )

        // [process to make TextView by Java code]
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        // LinearLayout is one of the ViewGroup class
        ViewGroup layout = findViewById(R.id.display_Linear);
        // add textView on that Linear layout
        layout.addView(textView);

    }
}