package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by arnou on 20-2-2018.
 */

public class Cleaner {

    Context fileContext;
    AppCompatActivity appView;
    ArrayList<Gebruiker> users = new ArrayList<>();
    ArrayList<Transaction> transactions = new ArrayList<>();
    ArrayList<String> commissies = new ArrayList<>();
    String selectedCommissie;
    public Integer MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    ArrayList<String> settings = new ArrayList<>();

    public Cleaner(Context fileContext, AppCompatActivity v){
        this.fileContext = fileContext;
        this.appView = v;
        readSettings();
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
            FileOutputStream file = fileContext.getApplicationContext().openFileOutput("users.txt", MODE_PRIVATE);
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
    public void WriteToCommissies(){
        try{
            File tester = fileContext.getApplicationContext().getFileStreamPath("commissies.txt");
            FileOutputStream file = fileContext.getApplicationContext().openFileOutput("commissies.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            for(int i = 0; i < commissies.size() ; i++){
                outputFile.write(commissies.get(i) + "\n");
            }
            outputFile.flush();
            outputFile.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void readCommissies(){
        commissies.clear();
        File file = fileContext.getApplicationContext().getFileStreamPath("commissies.txt");
        String lineFromFile;
        if(file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.getApplicationContext().openFileInput("commissies.txt")));
                int counter = 0;
                while ((lineFromFile = reader.readLine()) != null) {
                    commissies.add(lineFromFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            commissies = new ArrayList<>(Arrays.asList("HikCie", "DiesCie", "AmCie"));
            WriteToCommissies();
        }
        Log.w("commissies", commissies.toString());
    }
    public void setCommissie(@Nullable String c){
        final Button cButton = (Button)appView.findViewById(R.id.commissiesButton);
        if(c == null){
            cButton.setText(selectedCommissie);
        }else{
            Log.w("selectedCommissie", c);
            selectedCommissie = c;
            cButton.setText(c);
            if(settings.size() == 2){
                WriteToSettings();
                readSettings();
            }
            settings.set(2, c);
            WriteToSettings();
        }
    }

    public void readSettings() {
        settings.clear();
        //deleteFile("settings.txt");
        File file = fileContext.getFileStreamPath("settings.txt");
        String lineFromFile;
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.openFileInput("settings.txt")));
                int counter = 0;
                while ((lineFromFile = reader.readLine())!= null){
                    settings.add(lineFromFile.split(":")[1]);
                }
                Log.w("settings", settings.toString());
            }catch(IOException e){
                Toast.makeText(fileContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            WriteToSettings();
            readSettings();
        }

        if(settings.size() >= 3 && settings.get(2) != null)
        {
            selectedCommissie = settings.get(2);
            setCommissie(null);
        }
        Log.w("settings", settings.toString());
    }

    public void WriteToSettings(){
        if(settings.size() != 3){
            settings = new ArrayList<>(Arrays.asList("defaultemail@default.com","2.00", "HikCie"));
        }
        try{
            FileOutputStream myfile = fileContext.openFileOutput("settings.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(myfile);
            for(int i = 0; i < settings.size() ; i++){
                if(i == 0)
                {
                    outputFile.write("E:" + settings.get(0) + "\n");
                }
                else if(i==1){
                    outputFile.write("C:" + settings.get(1) + "\n");
                }
                else if(i==2){
                    outputFile.write("SC:" + settings.get(2));
                }
            }
            outputFile.flush();
            outputFile.close();
            Log.w("fileWritten", "settings fileWritten");

        }catch(IOException e){
            Toast.makeText(fileContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void readUserFile(){
        users.clear();
        File file = fileContext.getApplicationContext().getFileStreamPath("users.txt");
        //deleteFile("data.txt");
        String lineFromFile;
        String naam = "";
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.getApplicationContext().openFileInput("users.txt")));
                int counter = 0;
                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");

                    ArrayList<String> myArray = new ArrayList<String>();
                    while (tokens.hasMoreTokens()){
                        myArray.add(tokens.nextToken());
                    }
                    Log.i("USER", myArray.toString());
                    if(myArray.size() == 5) {
                        try {
                            naam = myArray.get(0);
                            String email = myArray.get(1);
                            String IBAN = myArray.get(2);
                            Double Kosten = Double.parseDouble(myArray.get(3));
                            String uniqueToken = myArray.get(4);

                            Gebruiker geb = new Gebruiker(naam, email, IBAN, Kosten, uniqueToken);
                            users.add(geb);
                        } catch (NumberFormatException e) {
                            Toast.makeText(fileContext.getApplicationContext(), "failed to parse user: " + naam, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(fileContext.getApplicationContext(), "failed to parse user: " + myArray.toString(), Toast.LENGTH_SHORT).show();
                    }
                    counter++;
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
                        Transaction trans = new Transaction(tokens.nextToken(), tokens.nextToken(), Double.parseDouble(tokens.nextToken()), tokens.nextToken(), tokens.nextToken(), tokens.nextToken());
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

    public void WriteTransactionFile(){
        try{
            FileOutputStream file = fileContext.openFileOutput("transactions.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            for(int i = 0 ; i < transactions.size() ; i++){
                outputFile.write(transactions.get(i).getName() + "," + transactions.get(i).getEmail() + "," + transactions.get(i).getPrize().toString() + "," + transactions.get(i).getTimestamp() +  "," + transactions.get(i).getOrder() + "," + transactions.get(i).getCommissie() + "\n");
            }
            outputFile.flush();
            outputFile.close();
        }catch(IOException e){
            Toast.makeText(fileContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void write_external_userfile(){
        readUserFile();
        File root = android.os.Environment.getExternalStorageDirectory();
        Log.i("MEDIA", root.getAbsolutePath().toString());
        File dir = new File (root.getAbsolutePath() + "/Documents");
        dir.mkdirs();
        File file = new File(dir, "Users.csv");
        file.delete();
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Naam;Saldo;IBAN;Email;UniqueToken");
            //Toast.makeText(this.fileContext.getApplicationContext(), Integer.toString(users.size()), Toast.LENGTH_SHORT).show();
            for(int i =0; i < users.size(); i++){
                //if(users.get(i).getKosten() > 0){
                    pw.println(users.get(i).getName() + ";" + String.format(Locale.US, "%.2f",users.get(i).getKosten()) + ";" + users.get(i).getIBAN() + ";" + users.get(i).getEmail() + ";" + users.get(i).getUniqueToken());
                //}
            }
            pw.flush();
            pw.close();
            f.close();
            Toast.makeText(this.fileContext.getApplicationContext(),"File written to "+file, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("MEDIA", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            Toast.makeText(this.fileContext.getApplicationContext(),"Something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void write_external_transactionfile(){
        readTransactionfile();
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
                pw.println(transactions.get(i).getName() + ";" + transactions.get(i).getEmail() + ";" + String.format(Locale.US, "%.2f",transactions.get(i).getPrize()) + ";" + transactions.get(i).getTimestamp() + ";" + transactions.get(i).getOrder());
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


    public void openFileDialog(){
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        appView.startActivityForResult(fileIntent, 10);
    }

    public void read_external_userfile(String path){

        if (ContextCompat.checkSelfPermission(appView, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appView, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }

        Log.i("padje", path);
        /*File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Documents");*/
        File file = new File(path);
        users.clear();
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String lineFromFile;
                reader.readLine();
                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ";");
                    Gebruiker geb;
                    try {

                        String naam = tokens.nextToken();
                        Double kosten = Double.parseDouble(tokens.nextToken());
                        String IBAN = tokens.nextToken();
                        String email = tokens.nextToken();
                        String uniqueToken = tokens.nextToken();
                        geb = new Gebruiker(naam, email, IBAN, kosten, uniqueToken);
                        users.add(geb);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                WriteToUserFile();
            }catch(IOException e){
                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }
    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}
