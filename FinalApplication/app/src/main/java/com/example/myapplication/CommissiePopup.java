package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class CommissiePopup extends AppCompatActivity {
    private Context mContext;
    public Cleaner cleaner;
    commissieAdapter cAdapter;
    public PopupWindow popupWindow;

    public CommissiePopup(Context fileContext, Cleaner cl, AppCompatActivity a){
        cleaner = cl;
        //cleaner.WriteToCommissies(c);
        cleaner.readCommissies();
        if(cleaner.selectedCommissie != null){
            Log.w("selectedCommissie", cleaner.selectedCommissie);
            cleaner.setCommissie(null);
        }else{
            cleaner.setCommissie(cleaner.commissies.get(0));
        }


        mContext = fileContext;
        cAdapter = new commissieAdapter(mContext);
        Button mButton = (Button) a.findViewById(R.id.commissiesButton);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });


    }
    public class commissieAdapter extends BaseAdapter {
        private Context mContext;

        public commissieAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return cleaner.commissies.size();
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
                text.setText(cleaner.commissies.get(position));
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setTextColor(Color.WHITE);
                text.setPadding(10,30,10,30);
                text.setTextSize(20);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        cleaner.setCommissie(text.getText().toString());
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


