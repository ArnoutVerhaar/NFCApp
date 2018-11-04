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
    UserAdapter customAdapter = new UserAdapter();
    Cleaner cl = null;

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
        cl = new Cleaner(getApplicationContext());

        cl.readUserFile();

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

        //cl.read_external_userfile();
        //customAdapter.notifyDataSetChanged();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Data?");
        builder.setMessage("Weet je het zeker? Alle transacties worden verwijderd! \n\nTyp 'VERWIJDER' om door te gaan!");
        final EditText removetext = new EditText(this);
        removetext.setHint("Typ hier");
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(removetext);
        builder.setView(layout);



        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if(removetext.getText().toString().equals("VERWIJDER")) {
                    Toast.makeText(getApplicationContext(), "Data is verwijderd!", Toast.LENGTH_SHORT).show();
                    cl.deleteTransactions();
                    cl.clearUsers();
                    customAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Text kwam niet overeen. Data niet verwijderd!", Toast.LENGTH_SHORT).show();
                }
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
        cl.readUserFile();
        customAdapter.notifyDataSetChanged();
    }

    class UserAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cl.users.size();
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

            tname.setText(cl.users.get(i).getName());
            tprize.setText("€" + String.format(Locale.US, "%.2f",cl.users.get(i).getKosten()) + ",-");



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
                    builder.setMessage("Email: " + cl.users.get(i).getEmail() + "\nIBAN: " +  cl.users.get(i).getIBAN() + "\nSaldo: €" + String.format(Locale.US, "%.2f",cl.users.get(i).getKosten()) + ",-")
                            .setTitle(cl.users.get(i).getName());

                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String POPUP_LOGIN_TITLE="Verander Gebruiker";
                            final String POPUP_LOGIN_TEXT="Voer naam en email en IBAN van de gebruiker in!";
                            AlertDialog.Builder alert = new AlertDialog.Builder(UsersActivity.this);

                            alert.setTitle(POPUP_LOGIN_TITLE);
                            alert.setMessage(POPUP_LOGIN_TEXT);

                            // Set an EditText view to get user input
                            final EditText name = new EditText(UsersActivity.this);
                            name.setText(cl.users.get(i).getName());
                            name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                            final EditText email = new EditText(UsersActivity.this);
                            email.setText(cl.users.get(i).getEmail());
                            email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            final EditText IBAN = new EditText(UsersActivity.this);
                            IBAN.setText(cl.users.get(i).getIBAN());
                            IBAN.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(name);
                            layout.addView(email);
                            layout.addView(IBAN);
                            alert.setView(layout);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    cl.users.get(i).setName(name.getText().toString());
                                    cl.users.get(i).setEmail(email.getText().toString());
                                    cl.users.get(i).setIBAN(IBAN.getText().toString());
                                    customAdapter.notifyDataSetChanged();
                                    cl.WriteToUserFile();// Do something with value!

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
                            if(cl.users.get(i).getKosten() == 0.00){
                                cl.users.remove(i);
                                customAdapter.notifyDataSetChanged();
                                cl.WriteToUserFile();// Do something with value!
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
