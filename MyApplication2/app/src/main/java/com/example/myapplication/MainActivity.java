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
import android.os.Environment;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MEDIA";
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
    public ImageAdapter myAdapter;
    public GridView gridview;
    float dpHeight,dpWidth;
    ArrayList<String> bestelling;

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

        gridview = (GridView) findViewById(R.id.btnGrid);
        gridview.setAdapter(new ImageAdapter(this));

        bestelling = new ArrayList<String>();

        users = new ArrayList<Gebruiker>();
        readUserfile();
        WriteToUserFile();



        //write_external_userfile();

        transactions= new ArrayList<Transaction>();
        readTransactionfile();
        //write_external_transactionfile();

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
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels;
        dpWidth  = outMetrics.widthPixels;
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


    public void read_external_userfile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Documents");
        File file = new File(dir, "Users.csv");

        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("users.txt")));
                String lineFromFile;
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
        //Toast.makeText(getApplicationContext(),"\n\nFile written to "+file, Toast.LENGTH_SHORT).show();
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

    private void readUserfile() {
        users.clear();
        File file = getApplicationContext().getFileStreamPath("users.txt");
        //deleteFile("data.txt");

        if(file.exists()){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("users.txt")));
                String lineFromFile;
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
                    WriteTransaction(foundUser,totalPrize,false,0.00);
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
        name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        name.setHint("  Naam");
        final EditText email = new EditText(this);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint("  Email");
        final EditText IBAN = new EditText(this);
        IBAN.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
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
                    WriteTransaction(newUser,totalPrize,true,startBudget);
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

    private void WriteTransaction(Gebruiker user, Double prize, Boolean newU,Double chip) {
        //Toast.makeText(getApplicationContext(), user.getName() + " : " + prize.toString(), Toast.LENGTH_SHORT).show();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm");
        String format = s.format(new Date());
        String order ="";
        for(int i =0; i< drinks.size(); i++){
            int occ = Collections.frequency(bestelling,drinks.get(i).getName());
            if (occ > 0)
                order += occ + " " + drinks.get(i).getName() + " en ";
        }
        if(newU){
            order += "1 kaart";
            prize += chip;
        }
        else
            order =  order.substring(0,order.length()-3);
        Toast.makeText(getApplicationContext(), order, Toast.LENGTH_SHORT).show();
        user.setKosten(user.getKosten() + totalPrize);
        transactions.add(0,new Transaction(user.getName(),prize,format,order));
        WriteToUserFile();
        bestelling.clear();
        totalPrize = 0.00;
        updatePrijs();


        try{
            FileOutputStream file = openFileOutput("transactions.txt", MODE_PRIVATE);
            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            Toast.makeText(getApplicationContext(), "Added to: " + transactions.get(0).getName(), Toast.LENGTH_SHORT).show();
            for(int i = 0 ; i < transactions.size() ; i++){

                outputFile.write(transactions.get(i).getName() + "," + transactions.get(i).getPrize().toString() + "," + transactions.get(i).getTimestamp() +  "," + transactions.get(i).getOrder() + "\n");
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
        super.onResume();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);


        readDrinkfile();

        gridview.setAdapter(new ImageAdapter(this));
        //fillButtons();
        //readTransactionfile();
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
        bestelling.clear();

    }

    public void PayButton(View v){
        pay = true;
    }

    /*public void fillButtons(){

        //TableLayout mTable;
        //TableRow tr = new TableRow(this);

        //mTable = (TableLayout) findViewById(R.id.tableLay);
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

    }*/
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

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return drinks.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            final Button myBtn;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                myBtn = new Button(mContext);

                myBtn.setLayoutParams(new GridView.LayoutParams((int)(dpWidth / 3 - 5)
                        , (int)dpHeight / 6));
                myBtn.setPadding(4, 4, 4, 4);



                myBtn.setText(drinks.get(position).getName() + "\n\n \u20ac" + String.format(Locale.US, "%.2f",drinks.get(position).getPrize()) + ",-");
                myBtn.setTag(String.format(Locale.US, "%.2f",drinks.get(position).getPrize()));
                myBtn.setBackgroundResource(R.drawable.bestelbutton);
                myBtn.setTextColor(Color.WHITE);
                myBtn.setTextSize(12);
                myBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        totalPrize += Double.parseDouble(v.getTag().toString());
                        updatePrijs();
                        bestelling.add(myBtn.getText().toString().split("\n")[0]);

                        //System.out.println("v.getid is:- " + v.getId());
                    }
                });
                //myBtn.setPadding(30,30,30,30);
            } else {
                myBtn = (Button) convertView;
            }


            myBtn.setBackgroundResource(R.drawable.bestelbutton);
            return myBtn;
        }
    }
}

