package com.example.songtolyrics.fragments;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ResponseOrionSuggestion;
import com.example.songtolyrics.view.MusicAdapter;
import com.example.songtolyrics.view.SimpleDividerItemDecoration;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListMusicFragment extends BaseFragment {
    private MusicAdapter mAdapter;

    private Boolean artistOrder;
    private Boolean songOrder;

    private RetrieveFeedTask mRunningTask;

    private String mTitle;
    private String mArtist;

    // Required empty public constructor
    public ListMusicFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mArtist = ListMusicFragmentArgs.fromBundle(getArguments()).getARTIST();
            mTitle = ListMusicFragmentArgs.fromBundle(getArguments()).getTITLE();
        }else{
            mTitle = "";
            mArtist = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParentview = inflater.inflate(R.layout.fragment_list_music, container, false);

        // RECYCLER VIEW
        RecyclerView mRecyclerView  = mParentview.findViewById(R.id.list_music_recycler_view);
        Button mAccueilBtn          = mParentview.findViewById(R.id.list_music_menu_button);

        List<Music> songList = new ArrayList<>();

        // If title is NOT defined -> read music from Local Storage
        if (mTitle.equals("")){
            // Read music from telephone (artist and title) and update content from history
            songList = Utils.updateLyrics(mContext, Utils.readSong(mContext));
        }
        // If title is defined -> search music from ORION API
        else{
            ProgressBar progressBar = mParentview.findViewById(R.id.list_music_progressBar);
            progressBar.setVisibility(View.VISIBLE);

            mRunningTask = new RetrieveFeedTask(mTitle, mArtist, getActivity());
            mRunningTask.execute();
        }

        // Populate recycler view
        mAdapter = new MusicAdapter(mContext, songList, mParentview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        mRecyclerView.setAdapter(mAdapter);


        // ===================================== //
        //                BUTTONS                //
        // ===================================== //

        // Return homepage
//        mAccueilBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        });

        // Add listener on order button (title - artist)
        setUpMusicOrderListener(songList, mParentview);

        return mParentview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel running task(s) to avoid memory leaks
        if (mRunningTask != null)
            mRunningTask.cancel(true);
    }

    private void setUpMusicOrderListener(List<Music> songList, View view){
        // Init song - artist order
        songOrder = true;
        artistOrder = true;

        Button mSortSongBtn = view.findViewById(R.id.list_music_music_order_button);
        Button mSortArtistesBtn = view.findViewById(R.id.list_music_artist_order_button);

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

            int drawable = songOrder ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow_upward_black_24dp;
            mSortSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0);
            mSortArtistesBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
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

            int drawable = artistOrder ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow_upward_black_24dp;
            mSortArtistesBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0);
            mSortSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        });
    }


    static class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        String song;
        String artist;
        private WeakReference<FragmentActivity> activityReference;

        RetrieveFeedTask(String s, String a, FragmentActivity context){
            song = s.replace(" ", "%20");
            artist = a.replace(" ", "%20");
            activityReference = new WeakReference<>(context);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation
            try {
                URL url = new URL(Parameters.ORION_API_URL_SUGGESTION + artist + "%20" + song + "&apikey=" + Parameters.ORION_API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(Parameters.REQUEST_TIMEOUT);
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
                return "";
            }
        }

        protected void onPostExecute(String response_str) {
            // get a reference to the activity if it is still there
            FragmentActivity activity = activityReference.get();

            // Check the current activity is still running
            if (activity != null){
                boolean success = !response_str.isEmpty();

                // Hide progress bar
                activityReference.get().findViewById(R.id.list_music_progressBar).setVisibility(View.GONE);

                if (success) {

                    Gson gson = new Gson();

                    try {
                        JSONObject jsonObject = new JSONObject(response_str);
                        ResponseOrionSuggestion response = gson.fromJson(jsonObject.toString(), ResponseOrionSuggestion.class);

                        if (response.isSuccess() && !response.getMusics().isEmpty()) {
                            List<Music> musics = Utils.updateLyrics(activity, response.getMusics());

                            View view = activity.findViewById(R.id.fragment_list_music);

                            // Populate recycler view
                            MusicAdapter mAdapter = new MusicAdapter(activity, musics, view);
                            RecyclerView mRecyclerView = activityReference.get().findViewById(R.id.list_music_recycler_view);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(activity));
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
                    builder.setMessage("Aucune suggestion n'a été trouvée. Veuillez reessayer avec une autre chanson et/ou artiste.")
                            .setPositiveButton("fermer", (dialog, id) -> {
                                //set what would happen when positive button is clicked
//                                activity.finish();
                            })
                            .show();
                }
            }
        }
    }
}
