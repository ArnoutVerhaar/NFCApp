package com.example.myapplication;

/**
 * Created by arnou on 11-9-2017.
 */

public class Drink {
    String name;
    Double prize;

    public Drink(String n, Double p){
        this.name=n;
        this.prize=p;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setPrize(Double prize){
        this.prize = prize;
    }

    public String getName(){
        return name;
    }
    public Double getPrize(){
        return prize;
    }
}
