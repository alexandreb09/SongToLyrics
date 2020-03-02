package com.example.songtolyrics.controler;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.songtolyrics.Model.Music;
import com.example.songtolyrics.R;
import com.example.songtolyrics.View.MusicAdapter;
import com.example.songtolyrics.View.SimpleDividerItemDecoration;

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
        final ArrayList<Music> songList = readSong();

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
        mAccueilBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Init artist order
        songOrder = false;
        // Update sort order from song
        mSortSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(songList);
                if (songOrder){
                    Collections.reverse(songList);
                }
                songOrder = !songOrder;
                mAdapter.notifyDataSetChanged();
            }
        });

        // Init artist order
        artistOrder = false;
        // Update sort order from artist
        mSortArtistesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }


    public ArrayList<Music> readSong(){
        ArrayList<Music> songList = new ArrayList<>();

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);

            //add songs to list
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Music(thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

        if (musicCursor != null) {
            musicCursor.close();
        }
        Collections.sort(songList);
        return songList;
    }
}
