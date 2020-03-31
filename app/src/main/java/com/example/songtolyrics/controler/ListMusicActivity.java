package com.example.songtolyrics.controler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.model.Artist;
import com.example.songtolyrics.model.Example;
import com.example.songtolyrics.model.Lyrics;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.ResponseOrionSuggestion;
import com.example.songtolyrics.view.MusicAdapter;
import com.example.songtolyrics.view.SimpleDividerItemDecoration;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class ListMusicActivity extends AppCompatActivity {

    MusicAdapter mAdapter;
    RecyclerView mRecyclerView;
    Button mAccueilBtn;
    Button mSortSongBtn;
    Button mSortArtistesBtn;

    Boolean artistOrder;
    Boolean songOrder;

    ListMusicActivity.RetrieveFeedTask runningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);

        // RECYCLER VIEW
        mRecyclerView           = findViewById(R.id.list_music_recycler_view);
        mAccueilBtn             = findViewById(R.id.list_music_menu_button);
        mSortSongBtn            = findViewById(R.id.list_music_music_order_button);
        mSortArtistesBtn        = findViewById(R.id.list_music_artist_order_button);

        final ArrayList<Music> songList;
        String title = "", artist = "";

        // Read parameters from previous activity is existing
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            title = extras.getString("title", "");
        }

        // If title is NOT defined -> read music from Local Storage
        if (title.equals("")){
            // Read music from telephone (artist and title)
            songList = Utils.readSong(this);
        }
        // If title is defined -> search music from ORION API
        else{
            artist = extras.getString("artist", "");

            ProgressBar progressBar = findViewById(R.id.list_music_progressBar);
            progressBar.setVisibility(View.VISIBLE);

            runningTask = new RetrieveFeedTask(title, artist, ListMusicActivity.this);
            runningTask.execute();

            songList = new ArrayList<>();
        }

        // Populate recycler view
        mAdapter = new MusicAdapter(this, songList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);



        // ===================================== //
        //                BUTTONS                //
        // ===================================== //

        // Return homepage
        mAccueilBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Init song - artist order
        songOrder = true;
        artistOrder = true;

        // Update sort order from song
        mSortSongBtn.setOnClickListener(v -> {
            Collections.sort(songList);
            if (songOrder){
                Collections.reverse(songList);
            }
            mAdapter.notifyDataSetChanged();
            // Update song - artist order
            songOrder = !songOrder;
            artistOrder = true;
        });

        // Update sort order from artist
        mSortArtistesBtn.setOnClickListener(v -> {
            // Sort list
            Collections.sort(songList, (m1, m2) -> {
                return m1.getArtist().compareToIgnoreCase(m2.getArtist()); // To compare string values
            });
            // Reverse if asked
            if (artistOrder){
                Collections.reverse(songList);
            }
            mAdapter.notifyDataSetChanged();
            // Update song - artist order
            songOrder = false;
            artistOrder = !artistOrder;
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
        private WeakReference<ListMusicActivity> activityReference;

        RetrieveFeedTask(String s, String a, ListMusicActivity context){
            song = s.replace(" ", "%20");
            artist = a.replace(" ", "%20");
            activityReference = new WeakReference<>(context);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation
            try {
                URL url = new URL(Parameters.ORION_API_URL_SUGGESTION + song + "%20" + artist + "&apikey=" + Parameters.ORION_API_KEY);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
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

        protected void onPostExecute(String response_str) {
            // get a reference to the activity if it is still there
            ListMusicActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){
                Boolean success = !response_str.isEmpty();

                // Hide progress bar
                activity.findViewById(R.id.list_music_progressBar).setVisibility(View.GONE);

                if (success) {

                    Gson gson = new Gson();

                    try {
                        JSONObject jsonObject = new JSONObject(response_str);
                        ResponseOrionSuggestion response = gson.fromJson(jsonObject.toString(), ResponseOrionSuggestion.class);

                        if (response.isSuccess() && !response.getResult().isEmpty()) {
                            Context context = activityReference.get();
                            // Populate recycler view
                            MusicAdapter mAdapter = new MusicAdapter(context, response.getResult());
                            RecyclerView mRecyclerView = activityReference.get().findViewById(R.id.list_music_recycler_view);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            success = false;
                        }
                    } catch (JSONException err) {
                        success = false;
                    }
                }

                if (!success){
                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder builder = new AlertDialog.Builder(activityReference.get());
                    builder.setMessage("Une erreur s'est produite")
                            .setPositiveButton("fermer", (dialog, id) -> {
                                // FIRE ZE MISSILES!
                            })
                            .setNegativeButton("Annuler", (dialog, id) -> {
                                // User cancelled the dialog
                            });
                    // Create the AlertDialog object and return it
                    builder.create();
                }
            }
        }
    }
}
