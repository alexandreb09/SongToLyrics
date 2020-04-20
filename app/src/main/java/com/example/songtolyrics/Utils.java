package com.example.songtolyrics;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ServerConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.example.songtolyrics.Parameters.API_PYTHON_FILENAME;
import static com.example.songtolyrics.Parameters.API_PYTHON_PORT;
import static com.example.songtolyrics.Parameters.API_PYTHON_URL;
import static com.example.songtolyrics.Parameters.DATA;
import static com.example.songtolyrics.Parameters.SIZE_MAX_HISTORY;
import static com.example.songtolyrics.Parameters.SIZE_MAX_HISTORY_KEY;
import static com.example.songtolyrics.Parameters.STORAGE_FAVORITES;
import static com.example.songtolyrics.Parameters.STORAGE_HISTORY;
import static com.example.songtolyrics.Parameters.STORAGE_PYTHON_SERVER_KEY;


public class Utils {

    /**
     * Read all songs from Local Phone Storage
     * @return list of songs (List<Music>)
     */
    public static List<Music> readSong(Context context){
        List<Music> songList = new ArrayList<>();

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

    /**
     * Check if a previous research have been done. Return
     *      - TRUE if song isn't available
     *      - FALSE if song is available
     * @param artist: artist name
     * @param title: music title
     * @param previousMusics: music search history
     * @return boolean: if research must be done
     */
    public static boolean doResearch(String artist, String title, List<Music> previousMusics){
        if (null != previousMusics){
            for (Music music : previousMusics){
                if (music.isAlreadySearch() && music.getArtist().equals(artist) && music.getTitle().equals(title)){
                    return music.isLyricsAvailable();
                }
            }
        }
        return true;
    }


    /**
     * Store the current list of musics (history)
     * @param context: Application context
     * @param musics_history: list of music to store
     */
    public static void storeMusicHistory(Context context, List<Music> musics_history){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DATA, MODE_PRIVATE);
        SharedPreferences.Editor listeEditor = mSharedPreferences.edit();

        String jsonMusic = new Gson().toJson(musics_history);
        listeEditor.putString(STORAGE_HISTORY, jsonMusic);
        listeEditor.apply();
    }

    /**
     * Get the history from storage
     * @param context: application context
     * @return list of musics: List<Music>
     */
    public static List<Music> restoreMusicHistory(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DATA, MODE_PRIVATE);

        Type type = new TypeToken<List<Music>>(){}.getType();
        String jsonMusics = mSharedPreferences.getString(STORAGE_HISTORY, null);
        List<Music> music = new Gson().fromJson(jsonMusics, type);
        if (null == music) music = new ArrayList<>();
        return music;
    }

    /**
     * Return index of the music in the list
     *      -> return -1 if not found
     * @param list: list of musics
     * @param music: music to search
     * @return int: index of the music in list
     */
    public static int indexMusic(final List<Music> list, final Music music){
        String title = music.getTitle(), artist = music.getArtist();
        for (int i = 0; i < list.size(); ++i){
            if (list.get(i).getTitle().equals(title) && list.get(i).getArtist().equals(artist)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Add the music to the lst of musics
     * Check the new list music size wth the maximum allowed: if greater, last elements are remove
     * @param context: Application context
     * @param musics: list of musics
     * @param music: new music to add
     * @return the new list of musics
     */
    public static List<Music> addMusic(Context context, List<Music> musics, final Music music){
        music.setAlreadySearch(true);
        // Add music
        musics.add(0, music);

        // Read hstoric size from storage
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DATA, MODE_PRIVATE);
        int size_max_historic = mSharedPreferences.getInt(SIZE_MAX_HISTORY_KEY, SIZE_MAX_HISTORY);
        // If music list size greater than the max: remove last elements
        if (musics.size() > size_max_historic){
            musics = musics.subList(0, size_max_historic);
        }
        return musics;
    }


    /**
     * Load history musics
     * Update the musics from the history
     * @param context: application context
     * @param musics: list of musics to update
     * @return list of musics updated
     */
    public static List<Music> updateLyrics(Context context, List<Music> musics){
        List<Music> history_musics = Utils.restoreMusicHistory(context);
        Music music;
        if (null != history_musics){
            for (Music history_music: history_musics){
                for (int i = 0; i < musics.size(); ++i){
                    music = musics.get(i);
                    if (history_music.getArtist().equals(music.getArtist()) && history_music.getTitle().equals(music.getTitle())){
                        music.setLyrics(history_music.getLyrics());
                        music.setAlreadySearch(true);
                        musics.set(i, music);
                    }
                }
            }
        }
        return musics;
    }




    // ======================================================//
    //                      FAVORITES                        //
    // ======================================================//
    public static List<Music> readFavoriteFromStorage(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DATA, MODE_PRIVATE);

        Type type = new TypeToken<List<Music>>(){}.getType();
        String jsonMusics = mSharedPreferences.getString(STORAGE_FAVORITES, "");
        List<Music> musics = new Gson().fromJson(jsonMusics, type);
        if (null == musics) musics = new ArrayList<>();
        return musics;
    }

    public static void addFavoriteAndStore(Context context, Music music_new){
        music_new.setAlreadySearch(true);

        SharedPreferences mSharedPreferences = context.getSharedPreferences(DATA, MODE_PRIVATE);
        SharedPreferences.Editor listeEditor = mSharedPreferences.edit();

        List<Music> musics = readFavoriteFromStorage(context);

        // If favorites isn't in history
        if (!musics.contains(music_new)){
            musics.add(music_new);

            String jsonMusics = new Gson().toJson(musics);
            listeEditor.putString(STORAGE_FAVORITES, jsonMusics);
            listeEditor.apply();
        }
    }
    public static void removeFavoriteAndStore(Context context, Music music_new){
        // Load favorites
        List<Music> musics = readFavoriteFromStorage(context);

        // If favorites isn't in history
        if (musics.contains(music_new)){
            // Remove element
            musics.remove(music_new);
            String jsonMusics = new Gson().toJson(musics);

            context.getSharedPreferences(DATA, MODE_PRIVATE)
                    .edit()
                    .putString(STORAGE_FAVORITES, jsonMusics)
                    .apply();
        }
    }

    // ======================================================//
    //                      TOAST ERROR                      //
    // ======================================================//
    public static void showToastError(Context context){
        Utils.showToastError(context, context.getResources().getString(R.string.error_message_default));
    }

    private static void showToastError(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    // ======================================================//
    //                    CHECK CONNEXION                    //
    // ======================================================//
    public static ServerConfig getPythonServerParams(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(DATA, MODE_PRIVATE);

        Type type = new TypeToken<ServerConfig>(){}.getType();
        String jsonServer = mSharedPreferences.getString(STORAGE_PYTHON_SERVER_KEY, "");
        ServerConfig serverConfig = new Gson().fromJson(jsonServer, type);
        if (null == serverConfig) serverConfig = new ServerConfig(API_PYTHON_URL, API_PYTHON_PORT, false);
        return serverConfig;
    }

    public static void storePythonServerParams(Context context, ServerConfig serverConfig){
        SharedPreferences.Editor editor = context.getSharedPreferences(DATA, MODE_PRIVATE).edit();

        String jsonServer = new Gson().toJson(serverConfig);
        editor.putString(STORAGE_PYTHON_SERVER_KEY, jsonServer);
        editor.apply();
    }


    //======================================
    public static void setToolbarTitle(Activity activity, String title){
        if (null != activity){
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (null != actionBar)
                actionBar.setTitle(title);
        }
    }
}
