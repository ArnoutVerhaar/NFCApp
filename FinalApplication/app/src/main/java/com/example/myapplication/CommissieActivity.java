package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class CommissieActivity extends baseActivity {

    private EditText txtinput;
    CustomAdapter customAdapter = new CustomAdapter();

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setBaseContentView(R.layout.addprize_layout);
        super.onCreate(savedInstanceState);


        final ListView listview = (ListView)findViewById(R.id.myList) ;
        listview.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);
    }


    public void addPrize(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nieuwe Commissie");
        alert.setMessage("Voer naam van de commissie in!");

        // Set an EditText view to get user input
        final EditText name = new EditText(this);
        name.setHint("  Naam");
        name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(name);
        alert.setView(layout);

        alert.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(name.getText().toString() != "") {
                    cl.commissies.add(name.getText().toString().replace("'", ""));
                    customAdapter.notifyDataSetChanged();
                    cl.WriteToCommissies();
                }

            }
        });
        alert.setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cl.commissies.size();
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

            tname.setText(cl.commissies.get(i));
            //tprize.setText("€" + String.format("%.2f",drinks.get(i).getPrize()) + ",-");

            //String myobj = drinks.get(i).getName();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommissieActivity.this);
                    builder.setTitle(cl.commissies.get(i));

                    builder.setNegativeButton("Wijzig", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String POPUP_LOGIN_TITLE="Verander commissie";
                            final String POPUP_LOGIN_TEXT="Voer nieuwe naam van de commissie in.";
                            AlertDialog.Builder alert = new AlertDialog.Builder(CommissieActivity.this);

                            alert.setTitle(POPUP_LOGIN_TITLE);
                            alert.setMessage(POPUP_LOGIN_TEXT);

                            // Set an EditText view to get user input
                            final EditText name = new EditText(CommissieActivity.this);
                            name.setText(cl.commissies.get(i));
                            name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(name);
                            alert.setView(layout);
                            alert.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String oldval = cl.commissies.get(i);
                                    String newval = name.getText().toString().replace("'", "");
                                    if(cl.commissies.indexOf(cl.selectedCommissie) == i){
                                        cl.setCommissie(newval);
                                    }
                                    cl.commissies.set(i, newval);
                                    customAdapter.notifyDataSetChanged();
                                    cl.WriteToCommissies();
                                    cl.readTransactionfile();
                                    for(int j = 0; j < cl.transactions.size() ; j++){
                                        if(cl.transactions.get(j).getCommissie().equals(oldval)){
                                            cl.transactions.get(j).setCommissie(newval);
                                        }
                                    }
                                    cl.WriteTransactionFile();
                                }
                            });
                            alert.setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            alert.show();
                        }
                    });
                    builder.setNeutralButton("Verwijder", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cl.commissies.remove(i);
                            customAdapter.notifyDataSetChanged();
                            cl.WriteToCommissies();
                        }
                    });
                    builder.setPositiveButton("Annuleer", new DialogInterface.OnClickListener() {
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


