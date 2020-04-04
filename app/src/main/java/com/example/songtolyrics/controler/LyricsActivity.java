
package com.example.songtolyrics.controler;


import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.R;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ResponseOrionLyrics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LyricsActivity extends AppCompatActivity {

    EditText mSongName;
    EditText mArtistName;
    TextView mResponseView;
    ProgressBar mProgressBar;
    Button mQueryButton;

    RetrieveFeedTask runningTask;
    Bundle mExtras;

    List<Music> mPreviousSearch;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        mResponseView = findViewById(R.id.responseView);
        mSongName = findViewById(R.id.songName);
        mArtistName = findViewById(R.id.artistName);
        mProgressBar = findViewById(R.id.progressBar);

        mQueryButton = findViewById(R.id.queryButton);

        mExtras = getIntent().getExtras();                                                          // Données de l'activité précédente
        if (null != mExtras){
            mArtistName.setText(mExtras.getString("artist"));
            mSongName.setText(mExtras.getString("title"));
        }

        // Load previous searched
        mPreviousSearch =  getPreviousMusicSearched();


        mQueryButton.setOnClickListener(v -> {
            String song = mSongName.getText().toString();
            String artist = mArtistName.getText().toString();

            // Check song field not empty
            if (TextUtils.isEmpty(song)){
                mSongName.setError( "Veuillez saisir le nom de la chanson" );
            }
            // Check artist field not empty
            else if (TextUtils.isEmpty(artist)){
                mArtistName.setError( "Veuillez saisir le nom de l'artiste" );
            }
            // Check song+artist not already search without success
            else if (Utils.doResearch(artist, song, mPreviousSearch)){
                mProgressBar.setVisibility(View.VISIBLE);
                mResponseView.setText("");

                runningTask = new RetrieveFeedTask(song, artist, LyricsActivity.this);
                runningTask.execute();
            }
            else{
                startNotFoundActivity(song, artist);
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

    /**
     * Load previous usicSearch from history
     * @return List<Music>: list of musics
     */
    private List<Music> getPreviousMusicSearched(){
        mSharedPreferences = getBaseContext().getSharedPreferences(Parameters.DATA, MODE_PRIVATE);
        List<Music> previousMusics = new ArrayList<>();

        if (mSharedPreferences.contains(Parameters.PREVIOUS_SEARCH_RESULTS)) {
            String jsonListProduit = mSharedPreferences.getString(Parameters.PREVIOUS_SEARCH_RESULTS, null);
            Type type = new TypeToken< List < Music >>() {}.getType();
            previousMusics = new Gson().fromJson(jsonListProduit, type);
        }
        return previousMusics;
    }


    /**
     * Start not found activity
     * @param song: song not found
     * @param artist: artist not found
     */
    public void startNotFoundActivity(String song, String artist){
        // Prepare extras for new activity
        Bundle b = new Bundle();
        b.putString("title", song);
        b.putString("artist", artist);

        // Start not found activity
        Intent not_found_activity = new Intent(this, NotFoundActivity.class);
        not_found_activity.putExtras(b);
        startActivity(not_found_activity);
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
                // Build URL
                URL url = new URL(Parameters.ORION_API_URL_LYRICS + artist + "/" + song + "?" +"apikey=" + Parameters.ORION_API_KEY);
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

                // Check response content
                if(response == null) {
                    // Start not found activity
                    activity.startNotFoundActivity(song, artist);

                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);
                }
                // If response isn't null <=> lyrics found
                else {
                    progressBar.setVisibility(View.GONE);
                    Log.i("INFO", response);

                    Gson gson = new Gson();

                    try {
                        // Get result from request
                        JSONObject jsonObject = new JSONObject(response).optJSONObject("result");
                        // Unserialized result to a ResponseOrionLyrics object
                        ResponseOrionLyrics responseOrion = gson.fromJson(jsonObject.toString(), ResponseOrionLyrics.class);

                        TextView responseView  = activity.findViewById(R.id.responseView);
                        responseView.setText(responseOrion.getSong().getText());
                    }catch (JSONException err){
                        Log.d("Error", err.toString());
                    }
                }
            }
        }
    }
}

