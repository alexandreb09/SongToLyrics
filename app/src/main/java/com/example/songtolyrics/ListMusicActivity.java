package com.example.songtolyrics;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import java.util.ArrayList;

import Entity.Music;

public class ListMusicActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a MusicAdapter instance
        MusicAdapter myAdapter = new MusicAdapter(this, R.layout.song);                      // Déclaration myAdapter

        // Read music from telephone (artist and title)
        ArrayList<Music> songList = readSong();

        // Fill the adapter with music list
        myAdapter.setMusicList(songList);

        // Display adapter
        setListAdapter(myAdapter);
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

        return songList;
    }
}
