package com.example.calculator_quiz03;

import java.util.ArrayList;
import java.util.Random;
public class Equation {
    String equation ;
    ArrayList<Integer> choices = new ArrayList<>();
    int correctIdx ;
    public Equation(){
        setEquation();
    }
    public void setEquation(){
        Random random = new Random();
        int ranNum1 = random.nextInt(100);
        int ranNum2 = random.nextInt(100);
        correctIdx = random.nextInt(4);
        equation = ranNum1 +" "+ "+"+" "+ ranNum2;
        int correctAns = ranNum1 + ranNum2 ;

        for(int i =0 ; i < 4 ; i++) choices.add(random.nextInt(100));
        for(int i =0 ; i < 3 ; i++)
            for(int j=i+1 ; j< 4; j++)
                while(choices.get(i)==choices.get(j) || choices.get(i)==correctAns);
        choices.set(correctIdx, correctAns ) ;
    }
}

