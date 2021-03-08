package com.example.myapplication;

/**
 * Created by arnou on 5-9-2017.
 */

public class Gebruiker {
    private String naam;
    private String email;
    private Double kosten;
    private String uniqueToken;

    public Gebruiker(String naam, String email, Double kosten,String uniqueToken){
        this.naam = naam;
        this.email = email;
        this.kosten = kosten;
        this.uniqueToken=uniqueToken;
    }

    public void setName(String name){
        this.naam = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setKosten(Double kosten){
        this.kosten = kosten;
    }
    public void setUniqueToken(String uniqueToken){this.uniqueToken=uniqueToken;}

    public String getName(){
        return naam;
    }
    public String getEmail(){
        return email;
    }
    public Double getKosten(){
        return kosten;
    }
    public String getUniqueToken(){return uniqueToken;}
}
