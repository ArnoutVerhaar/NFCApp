package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by arnou on 10-9-2017.
 */


public class TransactionActivity extends baseActivity {

    TransactionAdapter customAdapter = new TransactionAdapter();
    ArrayList<Integer> selectedTransactions;
    public PopupWindow popupWindow;
    public ArrayList<String> colourArray = new ArrayList<>(Arrays.asList("#b8d8ba", "#D9DBBC", "#ef959d", "#69585f", "#97B1A6", "#C9C5BA", "#51344D", "#93BEDF", "#228CDB", "#545C52"));

    public interface AsyncResponse {
        void processFinish(Object output);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.setBaseContentView(R.layout.transactions_layout);
        super.onCreate(savedInstanceState);
        selectedTransactions = new ArrayList<>();
        cl.readTransactionfile();
        cl.readUserFile();

        final ListView listview = (ListView)findViewById(R.id.translist) ;

        listview.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);
    }

    public void sendMail(View v){
        cl.readTransactionfile();
        cl.readUserFile();
        //cl.write_external_transactionfile();
        //cl.write_external_userfile();
        findViewById(R.id.sendmail).setEnabled(false);
        JSONObject json = createJSONobject(cl.users,cl.transactions);
        Log.d("Response:", String.valueOf(cl.transactions.size()));
        AsyncT asyncT = new AsyncT(json, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            String str = (String) output;
            if(str.equals("successfully inserted ")){
                cl.deleteTransactions();
                cl.clearUsers();
                findViewById(R.id.sendmail).setEnabled(true);
                Toast.makeText(getApplicationContext(),"Verzenden geslaagd!" , Toast.LENGTH_LONG).show();
                customAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(getApplicationContext(), str , Toast.LENGTH_LONG).show();
            }
            Log.d("Response:", (String) output);
            }
        });
        asyncT.execute();
    }
    public void notifyFloatingButton(){
        if(selectedTransactions.size() > 0){
            findViewById(R.id.setCommissieButton).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.setCommissieButton).setVisibility(View.GONE);
        }
    }
    public void openCommissiesPopup(View v){
        Context mContext = getApplicationContext();
        commissieAdapter cAdapter = new commissieAdapter(mContext);;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        final ListView listview = (ListView)popupView.findViewById(R.id.commissieLijst);
        listview.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
        listview.setItemsCanFocus(true);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
    public void changeCommissieForSelected(String c) {
        for (Integer i : selectedTransactions) {
            cl.transactions.get(i).setCommissie(c);
        }
        cl.WriteTransactionFile();
        selectedTransactions.clear();
        notifyFloatingButton();
        customAdapter.notifyDataSetChanged();

    }

    public JSONObject createJSONobject(ArrayList<Gebruiker> clUsers, ArrayList<Transaction> clTransactions){
        JSONObject myJson = new JSONObject();
        JSONArray userArray = new JSONArray();
        for (Gebruiker user : clUsers) {
            if(user.getKosten() > 0){
                JSONObject myUser = new JSONObject();
                try {
                    myUser.put("naam", user.getName());
                    myUser.put("email", user.getEmail());
                    myUser.put("iban", user.getIBAN());
                    myUser.put("price", user.getKosten());
                    myUser.put("uniqueToken", user.getUniqueToken());
                    userArray.put(myUser);
                }catch (JSONException e){
                    e.printStackTrace();
                }
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
                myTrans.put("email", trans.getEmail());
                myTrans.put("commissie", trans.getCommissie());
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
            Button cColor = (Button) view.findViewById(R.id.commissieColor);

            String color = colourArray.get(cl.commissies.indexOf(cl.transactions.get(i).getCommissie()));
            if(selectedTransactions.contains(i)){
                cColor.setBackgroundColor(Color.parseColor("#add8e6"));
                cColor.setText("✓");
            }else{
                cColor.setBackgroundColor(Color.parseColor(color));
                cColor.setText(cl.transactions.get(i).getCommissie().substring(0,3));
            }
            tname.setText(cl.transactions.get(i).getName());
            tprize.setText("€" + String.format("%.2f",cl.transactions.get(i).getPrize()) + ",-");
            tstamp.setText(cl.transactions.get(i).getTimestamp());
            view.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    selectedTransactions.add(i);
                    notifyFloatingButton();
                    customAdapter.notifyDataSetChanged();
                    return true;
                };
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedTransactions.contains(i)){
                        selectedTransactions.remove(selectedTransactions.indexOf(i));
                        notifyFloatingButton();
                        customAdapter.notifyDataSetChanged();
                    }else if(selectedTransactions.size() > 0){
                        selectedTransactions.add(i);
                        notifyFloatingButton();
                        customAdapter.notifyDataSetChanged();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
                        builder.setMessage("Prijs: €" + String.format(Locale.US, "%.2f", cl.transactions.get(i).getPrize()) + ",-\nBestelling: " + cl.transactions.get(i).getOrder() + "\nTimestamp: " + cl.transactions.get(i).getTimestamp() + "\nCommissie: " + cl.transactions.get(i).getCommissie())
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

    public class commissieAdapter extends BaseAdapter {
        private Context mContext;

        public commissieAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return cl.commissies.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView text;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                text = new TextView(mContext);
                text.setText(cl.commissies.get(position));
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setTextColor(Color.WHITE);
                text.setPadding(10,30,10,30);
                text.setTextSize(20);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeCommissieForSelected(text.getText().toString());
                        popupWindow.dismiss();
                    }
                });
            } else {
                text = (TextView) convertView;
            }
            return text;
        }
    }

}
