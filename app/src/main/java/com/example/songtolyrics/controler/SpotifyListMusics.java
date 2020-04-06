package com.example.songtolyrics.controler;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songtolyrics.R;
import com.example.songtolyrics.connectors.SpotifySongService;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.view.MusicAdapter;
import com.example.songtolyrics.view.SimpleDividerItemDecoration;

import java.util.ArrayList;


public class SpotifyListMusics extends AppCompatActivity {

    private SpotifySongService songService;
    private ArrayList<Music> recentlyPlayedTracks;

    MusicAdapter mAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_list_music);

        songService = new SpotifySongService(getApplicationContext());

        mRecyclerView           = findViewById(R.id.list_music_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(SpotifyListMusics.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(SpotifyListMusics.this));

        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            displaySongs(recentlyPlayedTracks);
        });
    }


    private void displaySongs(ArrayList<Music> musics) {
        // RECYCLER VIEW
        mAdapter                = new MusicAdapter(this, musics);
        mRecyclerView.setAdapter(mAdapter);
    }

}