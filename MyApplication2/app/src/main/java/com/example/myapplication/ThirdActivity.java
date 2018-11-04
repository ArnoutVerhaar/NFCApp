package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.transform.Result;

/**
 * Created by arnou on 10-9-2017.
 */


public class ThirdActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    public android.support.v7.widget.Toolbar mToolbar;
    TransactionAdapter customAdapter = new TransactionAdapter();
    ArrayList<String> settingsArray;
    Cleaner cl = null;


    public interface AsyncResponse {
        void processFinish(Object output);
    }

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

        cl = new Cleaner(getApplicationContext());
        cl.readTransactionfile();
        cl.readUserFile();

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

    public void sendMail(View v){

        cl.readTransactionfile();
        cl.readUserFile();

        //cl.write_external_transactionfile();
        //cl.write_external_userfile();
        findViewById(R.id.sendmail).setEnabled(false);
        JSONObject json = createJSONobject(cl.users,cl.transactions);
            /*File root = android.os.Environment.getExternalStorageDirectory();
            // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

            File dir = new File (root.getAbsolutePath() + "/Documents");
            dir.mkdirs();
            File file = new File(dir, "json.txt");
            file.delete();

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println("Naam;Bedrag;Tijdstip;Bestelling");
                pw.print(json.toString());
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("MEDIA", "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        Log.d("Response:", String.valueOf(cl.transactions.size()));
        AsyncT asyncT = new AsyncT(json, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            String str = (String) output;
            if(str.equals("successfully inserted ")){
                cl.deleteTransactions();
                cl.clearUsers();
                findViewById(R.id.sendmail).setEnabled(true);
                Toast.makeText(getApplicationContext(),"Verzenden geslaagd!" , Toast.LENGTH_SHORT).show();
                customAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(getApplicationContext(), str , Toast.LENGTH_SHORT).show();
            }
            Log.d("Response:", (String) output);
            }
        });
        asyncT.execute();

        //Intent emailIntent;
        /*String externalStoragePathStr = Environment.getExternalStorageDirectory().toString() + File.separatorChar;
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
        }*/
    }
    public JSONObject createJSONobject(ArrayList<Gebruiker> clUsers, ArrayList<Transaction> clTransactions){
        JSONObject myJson = new JSONObject();
        JSONArray userArray = new JSONArray();
        for (Gebruiker user : clUsers) {
            JSONObject myUser = new JSONObject();
            try {
                myUser.put("naam", user.getName());
                myUser.put("email", user.getEmail());
                myUser.put("iban", user.getIBAN());
                myUser.put("price", user.getKosten());
                userArray.put(myUser);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        JSONArray transactionArray = new JSONArray();
        for (Transaction trans : clTransactions) {
            JSONObject myTrans = new JSONObject();
            try {
                myTrans.put("naam", trans.getName());
                myTrans.put("order", trans.getOrder().replace("'",""));
                myTrans.put("price", trans.getPrize());
                myTrans.put("timestamp", trans.getTimestamp());
                transactionArray.put(myTrans);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        try {
            myJson.put("users", userArray);
            myJson.put("transactions", transactionArray);
            myJson.put("password", "8jm0aVBRCS92RBiLGaqt");
        }catch(JSONException e){
            e.printStackTrace();
        }
        return myJson;
    }

    @Override
    protected void onResume() {
        super.onResume();
        customAdapter.notifyDataSetChanged();
        cl.readTransactionfile();

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
            return cl.transactions.size();
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

            tname.setText(cl.transactions.get(i).getName());
            tprize.setText("€" + String.format("%.2f",cl.transactions.get(i).getPrize()) + ",-");
            tstamp.setText(cl.transactions.get(i).getTimestamp());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
                    builder.setMessage("Prijs: €" + String.format(Locale.US, "%.2f",cl.transactions.get(i).getPrize()) + ",-\nBestelling: " + cl.transactions.get(i).getOrder() +"\nTimestamp: " +cl.transactions.get(i).getTimestamp())
                            .setTitle(cl.transactions.get(i).getName());
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


    class AsyncT extends AsyncTask<Object,Object,Object>{

        public AsyncResponse delegate = null;//Call back interface
        JSONObject json;
        public AsyncT(JSONObject Json_obj, AsyncResponse asyncResponse){
            json=Json_obj;
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected Object doInBackground(Object... params) {
        String json_response = "";
                try {
                    URL url = new URL("https://www.sola-scriptura.nl/barmobiel.php"); //Enter URL here
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                    httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                    httpURLConnection.connect();
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    Log.d("Response:", json.toString());
                    wr.writeBytes(json.toString());
                    wr.flush();
                    wr.close();

                    InputStream inputStream;
                    int status = httpURLConnection.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK)
                        inputStream = httpURLConnection.getErrorStream();
                    else
                        inputStream = httpURLConnection.getInputStream();

                    InputStreamReader in = new InputStreamReader(inputStream);
                    BufferedReader br = new BufferedReader(in);
                    json_response += br.readLine();
                    httpURLConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return json_response;

        }

        @Override
        protected void onPostExecute(Object result) {
            delegate.processFinish(result);
        }
    }

}
