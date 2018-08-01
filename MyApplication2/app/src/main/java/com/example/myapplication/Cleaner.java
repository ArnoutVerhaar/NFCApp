package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by arnou on 20-2-2018.
 */

public class Cleaner {

    Context fileContext;
    ArrayList<Gebruiker> users = new ArrayList<>();
    ArrayList<Transaction> transactions = new ArrayList<>();
    public Cleaner(Context fileContext){
        this.fileContext = fileContext;
    }

    public void deleteTransactions(){
        fileContext.deleteFile("transactions.txt");
        transactions = new ArrayList<>();
    }

    public void clearUsers(){
        readUserFile();
        for (int i = 0; i < users.size(); i++) {
            users.get(i).setKosten(0.00);
        }
        WriteToUserFile();
    }

    public void WriteToUserFile(){
        try{
            FileOutputStream file = fileContext.getApplicationContext().openFileOutput("users.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            for(int i = 0; i < users.size() ; i++){
                outputFile.write(users.get(i).getName() + "," + users.get(i).getEmail()+ "," + users.get(i).getIBAN() + ","+ users.get(i).getKosten().toString() + "," + users.get(i).getUniqueToken() + "\n");
            }
            outputFile.flush();
            outputFile.close();


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void readUserFile(){
        users.clear();
        File file = fileContext.getApplicationContext().getFileStreamPath("users.txt");
        //deleteFile("data.txt");
        String lineFromFile;
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.getApplicationContext().openFileInput("users.txt")));

                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    Gebruiker geb = new Gebruiker(tokens.nextToken(),tokens.nextToken(),tokens.nextToken(),Double.parseDouble(tokens.nextToken()), tokens.nextToken());
                    users.add(geb);
                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void readTransactionfile() {
        transactions.clear();
        File file = fileContext.getApplicationContext().getFileStreamPath("transactions.txt");
        //deleteFile("data.txt");
        String lineFromFile;
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.getApplicationContext().openFileInput("transactions.txt")));
                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    try {
                        Transaction trans = new Transaction(tokens.nextToken(), Double.parseDouble(tokens.nextToken()), tokens.nextToken(), tokens.nextToken());
                        transactions.add(trans);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

    public void write_external_userfile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Documents");
        dir.mkdirs();
        File file = new File(dir, "Users.csv");
        file.delete();

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Naam;Saldo;IBAN;Email");
            for(int i =0; i < users.size(); i++){
                if(users.get(i).getKosten() > 0){
                    pw.println(users.get(i).getName() + ";" + String.format(Locale.US, "%.2f",users.get(i).getKosten()) + ";" + users.get(i).getIBAN() + ";" + users.get(i).getEmail());
                }
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("MEDIA", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(),"\n\nFile written to "+file, Toast.LENGTH_SHORT).show();
    }
    public void write_external_transactionfile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/Documents");
        dir.mkdirs();
        File file = new File(dir, "Transactions.csv");
        file.delete();

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Naam;Bedrag;Tijdstip;Bestelling");
            for(int i =0; i < transactions.size(); i++){
                pw.println(transactions.get(i).getName() + ";" + String.format(Locale.US, "%.2f",transactions.get(i).getPrize()) + ";" + transactions.get(i).getTimestamp() + ";" + transactions.get(i).getOrder());
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("MEDIA", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(),"\n\nFile written to "+file, Toast.LENGTH_SHORT).show();
    }

    public void read_external_userfile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Documents");
        File file = new File(dir, "Users.csv");

        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.getApplicationContext().openFileInput("users.txt")));
                String lineFromFile;
                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    Gebruiker geb;
                    try {
                        geb = new Gebruiker(tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), Double.parseDouble(tokens.nextToken()), tokens.nextToken());
                        users.add(geb);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }
        //Toast.makeText(getApplicationContext(),"\n\nFile written to "+file, Toast.LENGTH_SHORT).show();
    }
}
