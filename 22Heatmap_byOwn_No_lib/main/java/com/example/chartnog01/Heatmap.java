package com.example.chartnog01;
import android.widget.TextView;

import java.util.*;
public class Heatmap {
    Random random = new Random();
    ArrayList<Integer> num =new ArrayList<>() ;
    int[][] rgb = new int[80][3];

    public Heatmap(){
        for(int i=0; i<80; i++){
            this.num.add(random.nextInt(25001));
            this.rgb[i]=new int[3];
        }
        initNum();
    }
    public void initNum(){
        for (int i = 0; i < 80; i++){
            int number = this.num.get(i);
            if(number<1000){
                this.rgb[i][0] =(number/150 +number%150);
                this.rgb[i][1] =(250);
                this.rgb[i][2] =(100);
            }
            else if (number<5000){
                this.rgb[i][0] =(number/150 +number%150);
                this.rgb[i][1] =(200);
                this.rgb[i][2] =(100);
            }else if(number<7000){
                this.rgb[i][0] =(245+number/1000);
                this.rgb[i][1] =(150+number%50);
                this.rgb[i][2] =(0);
            }
            else if (number<10000){
                this.rgb[i][0] =(245+number/1000);
                this.rgb[i][1] =(150+number%150);
                this.rgb[i][2] =(0);            }
                        else if (number<15000){
                this.rgb[i][0] =(235 +number%15);
                this.rgb[i][1] =(100);
                this.rgb[i][2] =(50);            }
            else if (number<20000){
                this.rgb[i][0]=(200+number/1000);
                this.rgb[i][1]=(10);
                this.rgb[i][2]=(10);
            }
            else{
                this.rgb[i][0]=(200+number/500);
                this.rgb[i][1]=(0);
                this.rgb[i][2]=(0);
            }
        }
    }
}

