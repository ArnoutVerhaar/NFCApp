package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

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
        String naam = "";
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContext.getApplicationContext().openFileInput("users.txt")));
                int counter = 0;
                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    try {
                        naam = tokens.nextToken();
                        String email = tokens.nextToken();
                        String IBAN = tokens.nextToken();
                        Double Kosten = Double.parseDouble(tokens.nextToken());
                        String uniqueToken = tokens.nextToken();

                        Gebruiker geb = new Gebruiker(naam, email, IBAN, Kosten, uniqueToken);
                        users.add(geb);
                    }catch(NumberFormatException e){
                        Toast.makeText(fileContext.getApplicationContext(), "failed to parse user: " + naam, Toast.LENGTH_SHORT).show();
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
                        Transaction trans = new Transaction(tokens.nextToken(), tokens.nextToken(), Double.parseDouble(tokens.nextToken()), tokens.nextToken(), tokens.nextToken());
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


    public void read_external_userfile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Documents");
        File file = new File(dir, "Users.csv");
        users.clear();
        if(file.exists()){

            try{
                FileInputStream is = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String lineFromFile;
                reader.readLine();
                while ((lineFromFile = reader.readLine())!= null){
                    Log.i("MEDIA", lineFromFile);
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
