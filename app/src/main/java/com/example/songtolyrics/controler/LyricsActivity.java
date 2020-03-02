
package com.example.songtolyrics.controler;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import android.support.v7.app.AppCompatActivity;

import com.example.songtolyrics.Model.Example;
import com.example.songtolyrics.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class LyricsActivity extends AppCompatActivity {

    static final String API_KEY = "veVIEjZjElfDNV4XmWIsraUP9Cuu6Otfp5AUyKQVTG0E1Xx9xMFFDwR7odUzEbAW";
    static final String API_URL = "https://orion.apiseeds.com/api/music/lyric/";

    EditText mSongName;
    EditText mArtistName;
    TextView mResponseView;
    ProgressBar mProgressBar;
    Button mQueryButton;

    RetrieveFeedTask runningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_paroles);

        mResponseView = findViewById(R.id.responseView);
        mSongName = findViewById(R.id.songName);
        mArtistName = findViewById(R.id.artistName);
        mProgressBar = findViewById(R.id.progressBar);

        mQueryButton = findViewById(R.id.queryButton);

        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LyricsActivity.this, ErrorActivity.class);

                String song = mSongName.getText().toString();
                String artist = mArtistName.getText().toString();

                if (!TextUtils.isEmpty(song) && !TextUtils.isEmpty(artist)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mResponseView.setText("");

                    runningTask = new RetrieveFeedTask(song, artist, LyricsActivity.this);
                    runningTask.execute();
                }
                else if (TextUtils.isEmpty(song) || TextUtils.isEmpty(artist)){
                    startActivity(myIntent);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel running task(s) to avoid memory leaks
        if (runningTask != null)
            runningTask.cancel(true);
    }


    static class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        String song;
        String artist;

        private WeakReference<LyricsActivity> activityReference;

        RetrieveFeedTask(String s, String a, LyricsActivity context){
            song = s;
            artist = a;

            activityReference = new WeakReference<>(context);
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            // Do some validation
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
            // get a reference to the activity if it is still there
            LyricsActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){

                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                TextView responseView  = activity.findViewById(R.id.responseView);

                // Check response content
                if(response == null) {
                    Intent myIntent = new Intent(activity, NotFoundActivity.class);
                    activity.startActivity(myIntent);

                    progressBar.setVisibility(View.GONE);
                }
                // If response isn't null <=> lyrics found
                else {
                    progressBar.setVisibility(View.GONE);
                    Log.i("INFO", response);

                    Gson gson = new Gson();
                    Example object = gson.fromJson(response, Example.class);
                    responseView.setText(object.toString());
                }
            }
        }
    }
}

