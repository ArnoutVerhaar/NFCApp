package com.example.myapplication;

/**
 * Created by arnou on 11-9-2017.
 */

public class Transaction {

    String name;
    Double prize;
    String timestamp;
    String order;
    String email;
    String commissie;

    public Transaction(String n, String e, Double p, String t, String o, String c){
        this.name=n;
        this.prize=p;
        this.timestamp=t;
        this.order=o;
        this.email=e;
        this.commissie = c;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setPrize(Double prize){
        this.prize = prize;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public void setEmail(String email){ this.email = email;}
    public void setCommissie(String commissie){ this.commissie = commissie;}

    public String getName(){
        return name;
    }
    public Double getPrize(){
        return prize;
    }
    public String getOrder() {
        return order;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getEmail() {
        return email;
    }
    public String getCommissie() {
        return commissie;
    }
}
