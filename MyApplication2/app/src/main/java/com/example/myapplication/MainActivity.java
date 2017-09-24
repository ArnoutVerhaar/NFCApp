package com.example.myapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    public Double totalPrize = 0.00;

    private NfcAdapter nfcAdapter;
    public Boolean pay = false;
    ArrayList<Gebruiker> users;
    ArrayList<Drink> drinks;
    ArrayList<Transaction> transactions;
    ArrayList<String> settingsArray;
    public String defEmail = "defaultemail@default.com";
    public String defChipPrijs = "2.00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        drinks = new ArrayList<Drink>();
        readDrinkfile();

        users = new ArrayList<Gebruiker>();
        readUserfile();

        transactions= new ArrayList<Transaction>();
        readTransactionfile();

        settingsArray = new ArrayList<>();
        readSettingsfile();

        // NFC Tag handling

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this,
                    "NFC NOT supported on this devices!",
                    Toast.LENGTH_LONG).show();
            finish();
        }else if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            Toast.makeText(this,
                    "NFC is Enabled!",
                    Toast.LENGTH_LONG).show();
        }
        users = new ArrayList<Gebruiker>();
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
                    Transaction trans = new Transaction(tokens.nextToken(),Double.parseDouble(tokens.nextToken()), tokens.nextToken());
                    transactions.add(trans);
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
                    Gebruiker geb = new Gebruiker(tokens.nextToken(),tokens.nextToken(),tokens.nextToken(),Double.parseDouble(tokens.nextToken()), tokens.nextToken());
                    users.add(geb);
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


        }catch(IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void readDrinkfile(){
        drinks.clear();
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
    }
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(pay){
            Gebruiker foundUser = null;
            Tag myTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(myTag != null) {

                String uniqueID = bytesToHex(myTag.getId());
                readUserfile();
                for(int i = 0; i<users.size(); i++){
                    if(uniqueID.equals(users.get(i).getUniqueToken())){
                        foundUser =  users.get(i);
                    }
                }
                if(foundUser != null){
                    WriteTransaction(foundUser,totalPrize);
                }
                else {
                    createNewUser(uniqueID);
                }
            }
        }

    }

    public Gebruiker newUser;
    private void createNewUser(final String uniqueID) {
        final String POPUP_LOGIN_TITLE="Nieuwe Gebruiker";
        final String POPUP_LOGIN_TEXT="Voer uw naam en email-adres in!";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(POPUP_LOGIN_TITLE);
        alert.setMessage(POPUP_LOGIN_TEXT);

        // Set an EditText view to get user input
        final EditText name = new EditText(this);
        name.setHint("  Naam");
        final EditText email = new EditText(this);
        email.setHint("  Email");
        final EditText IBAN = new EditText(this);
        IBAN.setHint("  IBAN");
        final CheckBox buyChip = new CheckBox(this);
        buyChip.setText("Koop de chip voor \u20ac"+ settingsArray.get(1) + ",-");
        buyChip.setTextSize(18);



        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(name);
        layout.addView(email);
        layout.addView(IBAN);
        layout.addView(buyChip);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //fragment_obj.outputList.setText("For: " + name.getText().toString() + " with email = " + email.getText().toString());
                //getAlbumStorageDir("Hickie");
                Double startBudget;
                if(!name.getText().toString().matches("") || !email.getText().toString().matches("") || !IBAN.getText().toString().matches("")) {
                    if((buyChip.isChecked())){
                        startBudget = Double.parseDouble(settingsArray.get(1));
                    }
                    else{
                        startBudget = 0.00;
                    }
                    newUser = new Gebruiker(name.getText().toString(), email.getText().toString(), IBAN.getText().toString(), startBudget, uniqueID);
                    users.add(newUser);
                    Toast.makeText(getApplicationContext(), "User saved", Toast.LENGTH_SHORT).show();
                    //WriteToUserFile();// Do something with value!
                    WriteTransaction(newUser,totalPrize);
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

    private void WriteTransaction(Gebruiker user, Double prize) {
        //Toast.makeText(getApplicationContext(), user.getName() + " : " + prize.toString(), Toast.LENGTH_SHORT).show();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm");
        String format = s.format(new Date());

        user.setKosten(user.getKosten() + totalPrize);
        transactions.add(0,new Transaction(user.getName(),prize,format));
        WriteToUserFile();

        totalPrize = 0.00;
        updatePrijs();

        try{
            FileOutputStream file = openFileOutput("transactions.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            Toast.makeText(getApplicationContext(), "Added to: " + transactions.get(0).getName(), Toast.LENGTH_SHORT).show();
            for(int i = 0 ; i < transactions.size() ; i++){

                outputFile.write(transactions.get(i).getName() + "," + transactions.get(i).getPrize().toString() + "," + transactions.get(i).getTimestamp() + "\n");
            }
            outputFile.flush();
            outputFile.close();

            //Toast.makeText(getApplicationContext(), "User saved", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        pay = false;
    }


    @Override
    protected void onResume()
    {

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);

        super.onResume();
        readDrinkfile();
        fillButtons();
        readTransactionfile();
        readSettingsfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void updatePrijs(){
        TextView t = (TextView) findViewById(R.id.prijsView);
        t.setText("Prijs:        \u20ac" + String.format("%.2f",totalPrize) + ",-");
    }

    public void undo(View v){
        totalPrize = 0.00;
        updatePrijs();
        pay=false;
    }

    public void PayButton(View v){
        pay = true;
    }

    public void fillButtons(){
        TableLayout mTable;
        TableRow tr = new TableRow(this);

        mTable = (TableLayout) findViewById(R.id.tableLay);
        mTable.removeAllViews();
        int i =0;
        while (i < drinks.size()) {
            if (i % 3 == 0) {
                tr = new TableRow(this);
                mTable.addView(tr);
            }

            android.widget.TableRow.LayoutParams p = new android.widget.TableRow.LayoutParams();
            p.rightMargin = dpToPixel(6, getApplicationContext());
            p.leftMargin = dpToPixel(6, getApplicationContext());
            p.bottomMargin = dpToPixel(15, getApplicationContext());
            p.weight = 1;


            Button btn = new Button(this);
            btn.setText(drinks.get(i).getName() + "\n\n \u20ac" + String.format(Locale.US, "%.2f",drinks.get(i).getPrize()) + ",-");
            btn.setTag(String.format(Locale.US, "%.2f",drinks.get(i).getPrize()));
            btn.setBackgroundResource(R.drawable.bestelbutton);
            btn.setTextColor(Color.WHITE);
            btn.setPadding(30,30,30,30);
            btn.setLayoutParams(p);


            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    totalPrize += Double.parseDouble(v.getTag().toString());
                    updatePrijs();
                    //System.out.println("v.getid is:- " + v.getId());
                }
            });
            tr.addView(btn);
            i++;
        }

    }
    // DisplayHelper:
    private static Float scale;
    public static int dpToPixel(int dp, Context context) {
        if (scale == null)
            scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) dp * scale);
    }

    private void toast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT);
    }
}
