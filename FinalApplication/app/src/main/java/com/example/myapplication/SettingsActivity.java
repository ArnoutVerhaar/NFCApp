package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by arnou on 14-9-2017.
 */

public class SettingsActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    ArrayList<String> settingsArray;
    public String email;
    public String defEmail = "defaultemail@default.com";
    public String chipPrijs;
    public String defChipPrijs = "2.00";
    SettingsAdapter customAdapter = new SettingsAdapter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        mToolbar = (Toolbar) findViewById(R.id.nav_action);
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

        settingsArray = new ArrayList<>();
        readSettingsfile();

        final ListView listview = (ListView)findViewById(R.id.translist) ;
        listview.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);

    }

    private void readSettingsfile() {
        settingsArray.clear();
        //deleteFile("settings.txt");
        File file = getApplicationContext().getFileStreamPath("settings.txt");
        String lineFromFile;
        if(file.exists()){
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
        else{
            try{
                FileOutputStream myfile = openFileOutput("settings.txt", MODE_PRIVATE);
                OutputStreamWriter outputFile = new OutputStreamWriter(myfile);
                for(int i = 0; i < 2 ; i++){
                    if(i == 0)
                    {
                        settingsArray.add(defEmail);
                        outputFile.write("E:" + defEmail + "\n");
                    }
                    else if(i==1){
                        settingsArray.add(defChipPrijs);
                        outputFile.write("C: "+defChipPrijs);
                    }
                }
                outputFile.flush();
                outputFile.close();


            }catch(IOException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void WriteToSettings(){
        //settingsArray.clear();
        try{
            FileOutputStream myfile = openFileOutput("settings.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(myfile);
            for(int i = 0; i < settingsArray.size() ; i++){
                if(i == 0)
                {
                    outputFile.write("E:" + settingsArray.get(0) + "\n");
                }
                else if(i==1){
                    outputFile.write("C:" + settingsArray.get(1));
                }
            }
            outputFile.flush();
            outputFile.close();


        }catch(IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setDefaults(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Terugzetten Standaardinstellingen?");
        builder.setMessage("Alle standaardwaardes worden gereset!");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Defaults restored!", Toast.LENGTH_SHORT).show();
                deleteFile("settings.txt");
                readSettingsfile();
                customAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SettingsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return settingsArray.size();
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
            view = getLayoutInflater().inflate(R.layout.settings_item, null);
            TextView option = (TextView)view.findViewById(R.id.option);
            TextView value = (TextView)view.findViewById(R.id.value);

            String myopt = "";
            if(i == 0)
                myopt = "Email";
            else if(i == 1)
                myopt = "Chipprijs";

            option.setText(myopt);
            value.setText(settingsArray.get(i).toString());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (i) {
                        case 0:
                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                            builder.setTitle("Verander standaard Email!");
                            final EditText email = new EditText(SettingsActivity.this);
                            email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            email.setText(settingsArray.get(0));
                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(email);
                            builder.setView(layout);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    settingsArray.set(0, email.getText().toString());
                                    WriteToSettings();
                                    customAdapter.notifyDataSetChanged();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            builder.show();
                            return;
                        case 1:
                            AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                            alert.setTitle("Verander chipprijs!");
                            final EditText prize = new EditText(SettingsActivity.this);
                            prize.setText(settingsArray.get(1));
                            prize.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

                            LinearLayout layout1 = new LinearLayout(getApplicationContext());
                            layout1.setOrientation(LinearLayout.VERTICAL);
                            layout1.addView(prize);
                            alert.setView(layout1);
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    settingsArray.set(1, prize.getText().toString());
                                    WriteToSettings();
                                    customAdapter.notifyDataSetChanged();
                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            alert.show();
                            return;
                        default:
                            return;


                    }
                }

            });

            return view;
        }
    }
}
