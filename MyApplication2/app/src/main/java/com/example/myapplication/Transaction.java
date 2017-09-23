package com.example.myapplication;

/**
 * Created by arnou on 11-9-2017.
 */

public class Transaction {

    String name;
    Double prize;
    String timestamp;

    public Transaction(String n, Double p, String t){
        this.name=n;
        this.prize=p;
        this.timestamp=t;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setPrize(Double prize){
        this.prize = prize;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName(){
        return name;
    }
    public Double getPrize(){
        return prize;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
