package com.example.songtolyrics.controler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.view.MusicAdapter;
import com.example.songtolyrics.view.SimpleDividerItemDecoration;

public class ListMusicActivity extends AppCompatActivity {

    MusicAdapter mAdapter;
    RecyclerView mRecyclerView;
    Button mAccueilBtn;
    Button mSortSongBtn;
    Button mSortArtistesBtn;

    Boolean artistOrder;
    Boolean songOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);

        // Read music from telephone (artist and title)
        final ArrayList<Music> songList = Utils.readSong(this);

        // RECYCLER VIEW
        mRecyclerView           = findViewById(R.id.list_music_recycler_view);
        mAdapter                = new MusicAdapter(this, songList);
        mAccueilBtn             = findViewById(R.id.list_music_menu_button);
        mSortSongBtn            = findViewById(R.id.list_music_music_order_button);
        mSortArtistesBtn        = findViewById(R.id.list_music_artist_order_button);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);


        // Return homepage
        mAccueilBtn .setOnClickListener(v -> finish());

        // Init artist order
        songOrder = true;
        // Update sort order from song
        mSortSongBtn.setOnClickListener(v -> {
            Collections.sort(songList);
            if (songOrder){
                Collections.reverse(songList);
            }
            songOrder = !songOrder;
            mAdapter.notifyDataSetChanged();
            // Reset artist order to default value
            artistOrder = true;
        });

        // Init artist order
        artistOrder = true;
        // Update sort order from artist
        mSortArtistesBtn.setOnClickListener(v -> {
            Collections.sort(songList, new Comparator<Music>(){
                public int compare(Music m1, Music m2) {
                    return m1.getArtist().compareToIgnoreCase(m2.getArtist()); // To compare string values
                }
            });
            if (artistOrder){
                Collections.reverse(songList);
            }
            artistOrder = !artistOrder;
            mAdapter.notifyDataSetChanged();
            // Reset song order to default value
            songOrder = false;
        });
    }
}
