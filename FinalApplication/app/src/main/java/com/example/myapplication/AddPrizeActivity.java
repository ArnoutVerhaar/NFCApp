package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by arnou on 10-9-2017.
 */

public class AddPrizeActivity extends baseActivity {

    private EditText txtinput;
    ArrayList<Drink> drinks;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    CustomAdapter customAdapter = new CustomAdapter();

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setBaseContentView(R.layout.addprize_layout);
        super.onCreate(savedInstanceState);
        //readFile for drinks

        drinks = new ArrayList<Drink>();
        File file = getApplicationContext().getFileStreamPath("drinks.txt");
        String lineFromFile;
        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("drinks.txt")));

                while ((lineFromFile = reader.readLine())!= null){
                    StringTokenizer tokens = new StringTokenizer(lineFromFile, ",");
                    Drink geb = new Drink(tokens.nextToken(),Double.parseDouble(tokens.nextToken()));
                    drinks.add(geb);
                }

            }catch(IOException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        final ListView listview = (ListView)findViewById(R.id.myList) ;

        listview.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);
    }


    public void addPrize(View v) {

        final String POPUP_LOGIN_TITLE="Nieuwe Drank";
        final String POPUP_LOGIN_TEXT="Voer naam en prijs van de drank in!";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(POPUP_LOGIN_TITLE);
        alert.setMessage(POPUP_LOGIN_TEXT);

        // Set an EditText view to get user input
        final EditText name = new EditText(this);
        name.setHint("  Naam");
        name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        final EditText prijs = new EditText(this);
        prijs.setHint("  Prijs");
        prijs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(name);
        layout.addView(prijs);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if(prijs.getText().toString() != "" && name.getText().toString() != "") {
                    Drink myDrink = new Drink((name.getText().toString()).replace("'", ""), Double.parseDouble(prijs.getText().toString()));
                    drinks.add(myDrink);
                    customAdapter.notifyDataSetChanged();
                    WriteToFile();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }


    public void WriteToFile(){

        try{
            FileOutputStream file = openFileOutput("drinks.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            for(int i = 0; i < drinks.size() ; i++){
                outputFile.write(drinks.get(i).getName() + "," + drinks.get(i).getPrize().toString()+ ",\n");
            }
            outputFile.flush();
            outputFile.close();

            Toast.makeText(getApplicationContext(), "succesfully saved", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return drinks.size();
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
            view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView tname = (TextView)view.findViewById(R.id.txtitem);
            TextView tprize = (TextView)view.findViewById(R.id.prizeView);

            tname.setText(drinks.get(i).getName());
            tprize.setText("€" + String.format("%.2f",drinks.get(i).getPrize()) + ",-");

            String myobj = drinks.get(i).getName();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPrizeActivity.this);
                    builder.setMessage("€" + String.format(Locale.US, "%.2f",drinks.get(i).getPrize()) + ",-")
                            .setTitle(drinks.get(i).getName());

                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String POPUP_LOGIN_TITLE="Verander Drank";
                            final String POPUP_LOGIN_TEXT="Voer naam en prijs van de drank in!";
                            AlertDialog.Builder alert = new AlertDialog.Builder(AddPrizeActivity.this);

                            alert.setTitle(POPUP_LOGIN_TITLE);
                            alert.setMessage(POPUP_LOGIN_TEXT);

                            // Set an EditText view to get user input
                            final EditText name = new EditText(AddPrizeActivity.this);
                            name.setText(drinks.get(i).getName());
                            name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                            final EditText prijs = new EditText(AddPrizeActivity.this);
                            prijs.setText(String.format(Locale.US,"%.2f",drinks.get(i).getPrize()));
                            prijs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(name);
                            layout.addView(prijs);
                            alert.setView(layout);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    drinks.get(i).setName(name.getText().toString());
                                    drinks.get(i).setPrize(Double.parseDouble(prijs.getText().toString()));
                                    customAdapter.notifyDataSetChanged();
                                    WriteToFile();// Do something with value!

                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            alert.show();
                        }
                    });
                    builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            drinks.remove(i);
                            customAdapter.notifyDataSetChanged();
                            WriteToFile();// Do something with value!
                        }
                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
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


