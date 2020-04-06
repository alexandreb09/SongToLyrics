
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


import androidx.appcompat.app.AppCompatActivity;

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.R;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ResponseOrionLyrics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class LyricsActivity extends AppCompatActivity {

    EditText mSongName;
    EditText mArtistName;
    TextView mResponseView;
    ProgressBar mProgressBar;
    Button mQueryButton;

    RetrieveFeedTask runningTask;
    Bundle mExtras;

    List<Music> mMusicHistory;

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
        mMusicHistory =  Utils.restoreMusicHistory(this);



        mQueryButton.setOnClickListener(v -> {
            String song = mSongName.getText().toString();
            String artist = mArtistName.getText().toString();

            // Check song field not empty
            if (TextUtils.isEmpty(song)){
                mSongName.setError(getResources().getString(R.string.search_lyrics_error_title_missing));
            }
            // Check artist field not empty
            else if (TextUtils.isEmpty(artist)){
                mArtistName.setError(getResources().getString(R.string.search_lyrics_error_author_missing));
            }
            // Check song+artist not already search without success
            else if (Utils.doResearch(artist, song, mMusicHistory)){
                mProgressBar.setVisibility(View.VISIBLE);
                mResponseView.setText("");

                runningTask = new RetrieveFeedTask(song, artist, mMusicHistory, LyricsActivity.this);
                runningTask.execute();
            }
            // A similar research have already been done with no success
            //      -> directly redirect to not found activity
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
        List<Music> musicHistory;
        private WeakReference<LyricsActivity> activityReference;

        RetrieveFeedTask(String s, String a, List<Music> musicHistory_, LyricsActivity context){
            this.song = s;
            this.artist = a;
            this.musicHistory = musicHistory_;
            this.activityReference = new WeakReference<>(context);
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            String response = "{\"result\": {\"error\": \"Lyric no found, try again later.\"}}";

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
                    if (0 < stringBuilder.length()){
                        response = stringBuilder.toString();
                    }
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
            return response;
        }

        protected void onPostExecute(String response) {
            // get a reference to the activity if it is still there
            LyricsActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){

                // Hide progress bar
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                // Unserialize response
                ResponseOrionLyrics responseOrion;
                try {
                    // Get result from request
                    JSONObject jsonObject = new JSONObject(response).optJSONObject("result");
                    // Unserialized result to a ResponseOrionLyrics object
                    responseOrion = new Gson().fromJson(jsonObject.toString(), ResponseOrionLyrics.class);
                }catch (JSONException err){
                    Log.d("Error", err.toString());
                    responseOrion = new ResponseOrionLyrics("Aucune music disponible");
                }

                // If valid response
                if (responseOrion.isValid()){
                    // Show lyrics
                    TextView responseView  = activity.findViewById(R.id.responseView);
                    responseView.setText(responseOrion.getSong().getText());

                    // Add music to history and store it
                    Music music = new Music(responseOrion);
                    musicHistory = Utils.addMusic(activity, musicHistory, music);
                    Utils.storeMusicHistory(activity, this.musicHistory);
                }
                // If invalid response
                else{
                    Music music = new Music(song, artist);

                    // Add music to history and store it
                    musicHistory = Utils.addMusic(activity, musicHistory, music);
                    Utils.storeMusicHistory(activity, this.musicHistory);

                    // Start not found activity
                    activity.startNotFoundActivity(song, artist);
                }

            }
        }
    }
}

