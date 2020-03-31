package com.example.songtolyrics.controler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.songtolyrics.R;

public class NotFoundActivity extends Activity {

    Button mSuggestionBtn;
    static final String API_KEY = "veVIEjZjElfDNV4XmWIsraUP9Cuu6Otfp5AUyKQVTG0E1Xx9xMFFDwR7odUzEbAW";
    static final String API_URL = "https://orion.apiseeds.com/api/music/search/";
    Bundle mExtras;

    String artist, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notfound);

        mSuggestionBtn = findViewById(R.id.not_found_suggestion);
        mExtras = getIntent().getExtras();                                                          // Données de l'activité précédente
        if (null != mExtras){
            artist = mExtras.getString("artist", "");
            title = mExtras.getString("title", "");
        }

        mSuggestionBtn.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putString("title", title);
            b.putString("artist", artist);

            Intent list_music = new Intent(this, ListMusicActivity.class);
            list_music.putExtras(b);
            startActivity(list_music);
        });

    }
}
