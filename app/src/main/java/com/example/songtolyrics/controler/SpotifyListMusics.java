package com.example.songtolyrics.controler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.connectors.SpotifySongService;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.Song;
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

//        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
//        userView.setText(sharedPreferences.getString("userid", "No User"));

        mRecyclerView           = findViewById(R.id.list_music_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(SpotifyListMusics.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(SpotifyListMusics.this));


        getTracks();
    }


    private void getTracks() {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            displaySongs(recentlyPlayedTracks);
        });
    }

    private void displaySongs(ArrayList<Music> musics) {

        // Read music from telephone (artist and title)
//        final ArrayList<Music> songList = new ArrayList<>();
//        for (Song song: spotify_songs) {
//            songList.add( new Music(song.getName(), "Non disponible"));
//        }

        // RECYCLER VIEW
        mAdapter                = new MusicAdapter(this, musics);
        mRecyclerView.setAdapter(mAdapter);
    }

}