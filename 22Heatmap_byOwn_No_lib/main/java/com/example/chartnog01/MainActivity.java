package com.example.chartnog01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends AppCompatActivity {
    static ArrayList<TextView> arr = new ArrayList<>();
//    static ArrayList<Integer> num = new ArrayList<>();
//    static int[][] rgb=new int[48][3];
    private Timer timer;
    Random random = new Random();
    static Stack<Heatmap> heatmap = new Stack<>();


    public void setNum(){
        Heatmap now = heatmap.pop();
        for(int i=0; i<80; i++){
            arr.get(i).setText(""+now.num.get(i));
            arr.get(i).setTextColor(Color.parseColor("#ffffff"));
            arr.get(i).setBackgroundColor(Color.rgb(now.rgb[i][0],now.rgb[i][1],now.rgb[i][2]));
        }
        if(heatmap.size()<=5000){
            for(int i=0;i<10000;i++){
                Heatmap tem = new Heatmap();
                heatmap.add(tem);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // xml view 객체들 초기화
        Button btn = findViewById(R.id.btn); // 버튼 초기화
        // TextView 객체 ==>   ArrayList에 담아서 초기화
        for (int i = 1; i <= 80; i++) {// id 받기
            int id = getResources().getIdentifier("text" + i, "id", "com.example.chartnog01");
            TextView tem = findViewById(id);
            arr.add(tem);
        }
//       Intervalset=100ms이하로 내리면 Memory Error
        // Heatmap 클래스 객체 생성 --> Queue에 넣고싶은데 일단 Stack에 50개 미리 저장해서 하나씩 뽑아쓸것
        for (int i =0; i<10000;i++){
            Heatmap tem = new Heatmap();
            heatmap.add(tem);
        }
        //Stack List에 heatmap 객체 50개=> 48개의 random숫자,r,g,b int정보를 각각 가지고 있음.
        //Timer setting
        timer = new Timer();
        TimerTask adTast =new TimerTask() {
            @Override
            public void run() {
                setNum(); ;
            }
        };
        timer.schedule(adTast,0,50);
    }
    public void btnClick(View view) {
        timer.cancel();
        setNum();
    }
}
