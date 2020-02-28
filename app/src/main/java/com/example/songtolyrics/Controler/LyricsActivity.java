
package com.example.songtolyrics.Controler;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import android.support.v7.app.AppCompatActivity;

import com.example.songtolyrics.Model.Example;
import com.example.songtolyrics.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LyricsActivity extends AppCompatActivity {

    EditText songName;
    EditText artistName;
    TextView responseView;
    ProgressBar progressBar;
    ImageView imageView;
    static final String API_KEY = "veVIEjZjElfDNV4XmWIsraUP9Cuu6Otfp5AUyKQVTG0E1Xx9xMFFDwR7odUzEbAW";
    static final String API_URL = "https://orion.apiseeds.com/api/music/lyric/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_paroles);

        responseView = (TextView) findViewById(R.id.responseView);
        songName = (EditText) findViewById(R.id.songName);
        artistName = (EditText) findViewById(R.id.artistName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent myIntent = new Intent(LyricsActivity.this, ErreurActivity.class);

                    String str1 = LyricsActivity.this.songName.getText().toString();
                    String str2 = LyricsActivity.this.artistName.getText().toString();
                    if (!TextUtils.isEmpty(str1) && !TextUtils.isEmpty(str2))
                    {
                        new RetrieveFeedTask().execute();
                        return;
                    }
                    if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2))


                        startActivity(myIntent);
                        progressBar.setVisibility(View.GONE);

                }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        String song;
        String artist;
        protected void onPreExecute()
        {
            song = songName.getText().toString();
            artist = artistName.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");


        }

        protected String doInBackground(Void... urls) {
            //String email = emailText.getText().toString();
            // Do some validation here

            try {

                URL url = new URL(API_URL + artist + "/" + song + "?" +"apikey=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null)
            {
                Intent myIntent = new Intent(LyricsActivity.this, NonTrouveActivity.class);
                startActivity(myIntent);
                progressBar.setVisibility(View.GONE);
                return;


            }
            else
            {


                progressBar.setVisibility(View.GONE);
                Log.i("INFO", response);



                JSONObject jsonObject = null;
                Gson gson = new Gson();
                Example object = gson.fromJson(response, Example.class);
                responseView.setText(object.toString());





            }}
}}

