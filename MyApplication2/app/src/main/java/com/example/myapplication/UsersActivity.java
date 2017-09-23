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
 * Created by arnou on 12-9-2017.
 */

public class UsersActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    public android.support.v7.widget.Toolbar mToolbar;
    ArrayList<Gebruiker> users;
    UserAdapter customAdapter = new UserAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_layout);

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

        final ListView listview = (ListView)findViewById(R.id.translist) ;


        users = new ArrayList<Gebruiker>();
        readUserfile();

        listview.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ClearData(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Data?");
        builder.setMessage("Are you sure? All data will be lost!!!");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Data is deleted!", Toast.LENGTH_SHORT).show();
                deleteFile("transactions.txt");
                for(int i =0; i < users.size(); i++){
                    users.get(i).setKosten(0.00);
                }
                WriteToUserFile();
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

    @Override
    protected void onResume() {
        super.onResume();
        readUserfile();
        customAdapter.notifyDataSetChanged();
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


        }catch(IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Gebruiker geb = new Gebruiker(tokens.nextToken(),tokens.nextToken(),tokens.nextToken(),Double.parseDouble(tokens.nextToken()), tokens.nextToken());
                    users.add(geb);
                }

            }catch(IOException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UserAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
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
            view = getLayoutInflater().inflate(R.layout.list_item_user, null);
            TextView tname = (TextView)view.findViewById(R.id.txtitem);
            TextView tprize = (TextView)view.findViewById(R.id.prizeView);

            tname.setText(users.get(i).getName());
            tprize.setText("€" + String.format(Locale.US, "%.2f",users.get(i).getKosten()) + ",-");



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
                    builder.setMessage("Email: " + users.get(i).getEmail() + "\nIBAN: " +  users.get(i).getIBAN() + "\nSaldo: €" + String.format(Locale.US, "%.2f",users.get(i).getKosten()) + ",-")
                            .setTitle(users.get(i).getName());

                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String POPUP_LOGIN_TITLE="Verander Gebruiker";
                            final String POPUP_LOGIN_TEXT="Voer naam en email en IBAN van de gebruiker in!";
                            AlertDialog.Builder alert = new AlertDialog.Builder(UsersActivity.this);

                            alert.setTitle(POPUP_LOGIN_TITLE);
                            alert.setMessage(POPUP_LOGIN_TEXT);

                            // Set an EditText view to get user input
                            final EditText name = new EditText(UsersActivity.this);
                            name.setText(users.get(i).getName());
                            final EditText email = new EditText(UsersActivity.this);
                            email.setText(users.get(i).getEmail());
                            final EditText IBAN = new EditText(UsersActivity.this);
                            IBAN.setText(users.get(i).getIBAN());



                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(name);
                            layout.addView(email);
                            layout.addView(IBAN);
                            alert.setView(layout);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    users.get(i).setName(name.getText().toString());
                                    users.get(i).setEmail(email.getText().toString());
                                    users.get(i).setIBAN(IBAN.getText().toString());
                                    customAdapter.notifyDataSetChanged();
                                    WriteToUserFile();// Do something with value!

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
                            if(users.get(i).getKosten() == 0.00){
                                users.remove(i);
                                customAdapter.notifyDataSetChanged();
                                WriteToUserFile();// Do something with value!
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Saldo niet €0,-", Toast.LENGTH_SHORT).show();
                            }
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
