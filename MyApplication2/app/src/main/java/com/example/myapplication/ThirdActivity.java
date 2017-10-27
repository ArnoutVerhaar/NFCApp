package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by arnou on 10-9-2017.
 */

public class ThirdActivity extends AppCompatActivity {
    ArrayList<Transaction> transactions;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    public android.support.v7.widget.Toolbar mToolbar;
    TransactionAdapter customAdapter = new TransactionAdapter();
    ArrayList<Gebruiker> users;
    ArrayList<String> settingsArray;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_layout);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawer,R.string.open,R.string.close);

        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView mNavigationView;
        mNavigationView = (NavigationView) findViewById(R.id.nav_menu);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                mDrawer.closeDrawer((int) Gravity.LEFT);
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_payment:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        return true;
                    case R.id.nav_contact:
                        Intent account = new Intent(getApplicationContext(), ThirdActivity.class);
                        account.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(account);
                        return true;
                    case R.id.nav_addprize:
                        Intent intent4 = new Intent(getApplicationContext(), SecondActivity.class);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent4);
                        return true;
                    case R.id.nav_users:
                        Intent intent2 = new Intent(getApplicationContext(), UsersActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_settings:
                        Intent intent3 = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent3);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        return true;

                    default:
                        return true;
                }

            };


        });

        transactions = new ArrayList<Transaction>();
        readTransactionfile();

        users = new ArrayList<Gebruiker>();
        readUserfile();



        final ListView listview = (ListView)findViewById(R.id.translist) ;

        listview.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);

        settingsArray = new ArrayList<>();
        readSettingsfile();

    }

    private void readSettingsfile() {
        //deleteFile("settings.txt");
        File file = getApplicationContext().getFileStreamPath("settings.txt");
        String lineFromFile;
        if(file.exists()){
            settingsArray.clear();
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("settings.txt")));
                int counter = 0;
                while ((lineFromFile = reader.readLine())!= null){
                    settingsArray.add(lineFromFile.split(":")[1]);
                }

            }catch(IOException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readUserfile() {
        users.clear();
        File file = getApplicationContext().getFileStreamPath("users.txt");
        //deleteFile("data.txt");
        String lineFromFile;
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("users.txt")));

                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    Gebruiker geb;
                    try {
                        geb = new Gebruiker(tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), Double.parseDouble(tokens.nextToken()), tokens.nextToken());
                        users.add(geb);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }catch(IOException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void WriteToUserFile(){
        try{
            FileOutputStream file = openFileOutput("users.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            for(int i = 0; i < users.size() ; i++){
                outputFile.write(users.get(i).getName() + "," + users.get(i).getEmail()+ "," + users.get(i).getIBAN() + ","+ users.get(i).getKosten().toString() + "," + users.get(i).getUniqueToken() + "\n");
            }
            outputFile.flush();
            outputFile.close();

            Toast.makeText(getApplicationContext(), "succesfully saved", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void readTransactionfile() {
        transactions.clear();
        File file = getApplicationContext().getFileStreamPath("transactions.txt");
        //deleteFile("data.txt");
        String lineFromFile;
        if(file.exists()){

            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("transactions.txt")));
                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    try {
                        Transaction trans = new Transaction(tokens.nextToken(), Double.parseDouble(tokens.nextToken()), tokens.nextToken(), tokens.nextToken());
                        transactions.add(trans);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }catch(IOException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendMail(View v){

        readTransactionfile();
        readUserfile();

        write_external_transactionfile();
        write_external_userfile();

        //Intent emailIntent;
        String externalStoragePathStr = Environment.getExternalStorageDirectory().toString() + File.separatorChar;
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        //Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Documents/Users.csv"));
        //emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        String filePaths[] = {
                externalStoragePathStr + "Documents/Users.csv",
                externalStoragePathStr + "Documents/Transactions.csv"};
        ArrayList<Uri> uris = new ArrayList<Uri>();
        for (String file : filePaths) {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        String format = s.format(new Date());

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{settingsArray.get(0).toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Transactions at " + format);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ThirdActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onResume() {
        super.onResume();
        customAdapter.notifyDataSetChanged();
        readTransactionfile();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class TransactionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return transactions.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_timestamp, null);
            TextView tname = (TextView)view.findViewById(R.id.txtitem);
            TextView tprize = (TextView)view.findViewById(R.id.prizeView);
            TextView tstamp = (TextView) view.findViewById(R.id.timestamp);

            tname.setText(transactions.get(i).getName());
            tprize.setText("€" + String.format("%.2f",transactions.get(i).getPrize()) + ",-");
            tstamp.setText(transactions.get(i).getTimestamp());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
                    builder.setMessage("Prijs: €" + String.format(Locale.US, "%.2f",transactions.get(i).getPrize()) + ",-\nBestelling: " + transactions.get(i).getOrder() +"\nTimestamp: " +transactions.get(i).getTimestamp())
                            .setTitle(transactions.get(i).getName());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            });


            return view;
        }
    }
}
