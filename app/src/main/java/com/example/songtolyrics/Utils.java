package com.example.songtolyrics;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.example.songtolyrics.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.example.songtolyrics.Parameters.API_PYTHON_FILENAME;


public class Utils {

    /**
     * Read all songs from Local Phone Storage
     * @return list of songs (ArrayList<Music>)
     */
    public static ArrayList<Music> readSong(Context context){
        ArrayList<Music> songList = new ArrayList<>();

        ContentResolver musicResolver = context.getContentResolver();
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

                if (thisArtist.equals("<unknown>")){
                    thisArtist = "";
                }

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


    /**
     * Get path to the audio file recorded
     * @return path (String)
     */
    public static String getFileName(Context c){
        String fileName = "";
        // Record to the external cache directory for visibility
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                fileName = Objects.requireNonNull(c.getExternalCacheDir()).getAbsolutePath();
            }
        }catch (NullPointerException e){
            fileName = Environment.DIRECTORY_DOWNLOADS;
        }
        return fileName + ("/" + API_PYTHON_FILENAME);
    }
}
